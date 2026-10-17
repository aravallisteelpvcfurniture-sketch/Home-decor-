/**
 * Aravalli Steel PVC Furniture - Secure Gemini API Backend Proxy
 * Deployed as Firebase Cloud Function or Google Cloud Run service.
 * 
 * Security Features:
 * 1. Firebase Authentication: Requires valid Bearer ID Token for all calls.
 * 2. Secret Protection: Uses server-side GEMINI_API_KEY from Google Secret Manager.
 * 3. Rate Limiting: Prevents abuse and enforces per-user quotas.
 * 4. Input Sanitization: Rejects oversized or invalid prompts.
 * 5. Uses latest supported model: gemini-3.5-flash.
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const https = require("https");

if (!admin.apps.length) {
  admin.initializeApp();
}

// In-memory rate limiting map: userId -> [timestamp]
const rateLimits = new Map();
const MAX_REQUESTS_PER_MINUTE = 15;

function checkRateLimit(userId) {
  const now = Date.now();
  const windowMs = 60 * 1000;
  const userTimestamps = rateLimits.get(userId) || [];
  const validTimestamps = userTimestamps.filter(t => now - t < windowMs);

  if (validTimestamps.length >= MAX_REQUESTS_PER_MINUTE) {
    return false;
  }
  validTimestamps.push(now);
  rateLimits.set(userId, validTimestamps);
  return true;
}

exports.generateAiContent = functions.https.onRequest(async (req, res) => {
  // Set CORS headers
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Methods", "POST, OPTIONS");
  res.set("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    res.status(204).send("");
    return;
  }

  if (req.method !== "POST") {
    res.status(405).json({ error: "Method Not Allowed. Use POST." });
    return;
  }

  // 1. Authenticate Request via Firebase ID Token
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    res.status(401).json({
      error: "अनधिकृत अनुरोध (Unauthorized). मान्य Firebase Auth Bearer टोकन आवश्यक है।"
    });
    return;
  }

  const idToken = authHeader.split("Bearer ")[1];
  let decodedUser;
  try {
    decodedUser = await admin.auth().verifyIdToken(idToken);
  } catch (err) {
    res.status(403).json({
      error: "अमान्य प्रमाणीकरण टोकन (Invalid or expired authentication token)."
    });
    return;
  }

  const userId = decodedUser.uid;

  // 2. Enforce Rate Limiting
  if (!checkRateLimit(userId)) {
    res.status(429).json({
      error: "अनुरोध सीमा पार हो गई है (Rate limit exceeded). कृपया एक मिनट बाद पुनः प्रयास करें।"
    });
    return;
  }

  // 3. Input Validation
  const { prompt, toolType, language, targetModel } = req.body;
  if (!prompt || typeof prompt !== "string" || prompt.trim().length === 0) {
    res.status(400).json({ error: "प्रॉम्प्ट खाली नहीं हो सकता (Prompt is required)." });
    return;
  }

  if (prompt.length > 2500) {
    res.status(400).json({ error: "प्रॉम्प्ट बहुत लंबा है (Prompt exceeds 2500 characters limit)." });
    return;
  }

  // 4. Access Server-Side Secret Key
  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey) {
    res.status(500).json({
      error: "सर्वर पर जेमिनी API कुंजी कॉन्फ़िगर नहीं है (GEMINI_API_KEY secret is not set on the backend)."
    });
    return;
  }

  // Model selection: Default to approved gemini-3.5-flash
  const model = targetModel || "gemini-3.5-flash";

  // System instruction for Aravalli Furniture
  const systemInstruction = 
    "You are the expert AI Business and Design Assistant for ARAVALLI STEEL PVC FURNITURE located in Modasa, Gujarat, India. " +
    "You specialize in Stainless Steel Railing (SS 304/202), PVC Modular Kitchens, UPVC Windows, Aluminium Glass Balconies, and Modern Wardrobes. " +
    "Respond respectfully, professionally, and clearly. Default to the requested language (" + (language || "Hindi") + "), providing practical, high-value, ready-to-share business content.";

  // Use official Google GenAI SDK (@google/genai) if available
  let GoogleGenAI;
  try {
    const genaiPkg = require("@google/genai");
    GoogleGenAI = genaiPkg.GoogleGenAI;
  } catch (_) {
    GoogleGenAI = null;
  }

  if (GoogleGenAI) {
    try {
      const ai = new GoogleGenAI({ apiKey: apiKey });
      const response = await ai.models.generateContent({
        model: model,
        contents: prompt,
        config: {
          systemInstruction: systemInstruction,
          temperature: 0.7,
          topP: 0.95
        }
      });
      const generatedText = response.text || "कोई प्रतिक्रिया प्राप्त नहीं हुई।";
      res.status(200).json({
        success: true,
        model: model,
        text: generatedText,
        toolType: toolType || "general",
        sdk: "@google/genai"
      });
      return;
    } catch (sdkErr) {
      res.status(500).json({
        success: false,
        error: `जेमिनी SDK त्रुटि: ${sdkErr.message || "अनपेक्षित त्रुटि"}`
      });
      return;
    }
  }

  const requestBody = JSON.stringify({
    systemInstruction: {
      parts: [{ text: systemInstruction }]
    },
    contents: [
      {
        parts: [{ text: prompt }]
      }
    ],
    generationConfig: {
      temperature: 0.7,
      topP: 0.95
    }
  });

  const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;

  try {
    const apiReq = https.request(
      url,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Content-Length": Buffer.byteLength(requestBody)
        }
      },
      (apiRes) => {
        let data = "";
        apiRes.on("data", (chunk) => {
          data += chunk;
        });
        apiRes.on("end", () => {
          try {
            const parsed = JSON.parse(data);
            if (apiRes.statusCode >= 200 && apiRes.statusCode < 300) {
              const generatedText = parsed.candidates?.[0]?.content?.parts?.[0]?.text || "कोई प्रतिक्रिया प्राप्त नहीं हुई।";
              res.status(200).json({
                success: true,
                model: model,
                text: generatedText,
                toolType: toolType || "general"
              });
            } else {
              res.status(apiRes.statusCode).json({
                success: false,
                error: parsed.error?.message || "जेमिनी सेवा त्रुटि (Gemini API error)"
              });
            }
          } catch (e) {
            res.status(500).json({ error: "प्रतिक्रिया पार्सिंग त्रुटि (Error parsing response)" });
          }
        });
      }
    );

    apiReq.on("error", (e) => {
      res.status(500).json({ error: `नेटवर्क त्रुटि: ${e.message}` });
    });

    apiReq.write(requestBody);
    apiReq.end();
  } catch (err) {
    res.status(500).json({ error: `सर्वर त्रुटि: ${err.message}` });
  }
});
