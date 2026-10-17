# ARAVALLI STEEL PVC FURNITURE - Secure Gemini AI Setup Guide
## अरावल्ली स्टील पीवीसी फर्नीचर - सुरक्षित जेमिनी AI सेटअप गाइड

यह गाइड समझाता है कि Google Gemini AI को Aravalli Furniture Android ऐप में सुरक्षित रूप से कैसे कनेक्ट और कॉन्फ़िगर करें।

---

### 🛡️ सुरक्षा का नियम (Security Invariants)

1. **कभी भी अपनी Gemini API Key को Android सोर्स कोड, BuildConfig, या GitHub में हार्डकोड न करें।**
2. Android APK को आसानी से रिवर्स-इंजीनियर (Decompile) किया जा सकता है। क्लाइंट-साइड में रखी गई कोई भी चाबी असुरक्षित होती है।
3. **प्रोडक्शन आर्किटेक्चर**: ऐप सुरक्षित बैकएंड प्रॉक्सी (`backend/index.js`) से संपर्क करता है। बैकएंड उपयोगकर्ता के **Firebase Auth ID Token** की जांच करता है, और केवल प्रमाणित उपयोगकर्ताओं के लिए Google Cloud Secret Manager से सुरक्षित `GEMINI_API_KEY` के जरिए Gemini 3.5 Flash मॉडल को कॉल करता है।

---

### 🚀 चरण 1: Google AI Studio में Gemini API Key प्राप्त करें

1. [Google AI Studio](https://aistudio.google.com/) पर जाएं।
2. अपने Google खाते से लॉगिन करें।
3. ऊपर बाईं ओर **Get API key** पर क्लिक करें।
4. **Create API key** चुनें (नया प्रोजेक्ट या मौजूदा Google Cloud प्रोजेक्ट चुनें)।
5. अपनी API कुंजी को सुरक्षित स्थान पर कॉपी करें।

---

### 🔒 चरण 2: प्रोडक्शन बैकएंड (Firebase Cloud Functions) पर Key सेट करें

परियोजना में `backend/index.js` और `backend/package.json` पहले से तैयार हैं।

1. अपने कंप्यूटर पर टर्मिनल खोलें और `backend` डायरेक्टरी में जाएं:
   ```bash
   cd backend
   npm install
   ```
2. Firebase CLI द्वारा Secret Manager में अपनी Gemini API कुंजी सुरक्षित रूप से दर्ज करें:
   ```bash
   firebase functions:secrets:set GEMINI_API_KEY
   # पूछे जाने पर अपनी Google AI Studio API कुंजी पेस्ट करें
   ```
3. Cloud Function को डिप्लॉय करें:
   ```bash
   firebase deploy --only functions
   ```
4. डिप्लॉयमेंट के बाद आपको एक URL प्राप्त होगा:
   `https://<region>-<project-id>.cloudfunctions.net/generateAiContent`
5. Aravalli Furniture ऐप खोलें ➔ **मेनू** ➔ **AI सेटिंग्स** में जाएं और इस URL को पेस्ट करें।

---

### 🧪 चरण 3: AI Studio / लोकल डेवलपमेंट में तुरंत टेस्टिंग कैसे करें

यदि आप AI Studio डेवलपमेंट एनवायरनमेंट में तुरंत टेस्ट करना चाहते हैं:
1. AI Studio के **Secrets Panel** (या `.env` फ़ाइल) में `GEMINI_API_KEY` सेट करें:
   ```
   GEMINI_API_KEY=AIzaSy...
   ```
2. ऐप की `GeminiAiService` स्वचालित रूप से रनटाइम पर इस कुंजी को पढ़ेगी और `gemini-3.5-flash` को कॉल करेगी।

---

### 📱 ऐप में उपलब्ध 8 AI टूल्स (AI Features Available)

1. **AI पोस्टर जनरेटर (Poster Generator)**: त्योहारों और नए बंगलों के लिए आकर्षक प्रचार पोस्टर।
2. **उत्पाद विवरण लेखक (Product Description)**: एसएस रेलिंग (SS 304), 18mm पीवीसी किचन, टफन्ड ग्लास बालकनी के तकनीकी और ग्राहक-अनुकूल विवरण।
3. **मार्केटिंग कैप्शन (Marketing Captions)**: व्हाट्सएप स्टेटस, इंस्टाग्राम और फेसबुक के लिए हैशटैग युक्त कैप्शन (हिंदी, गुजराती, अंग्रेजी)।
4. **व्हाट्सएप प्रचार संदेश (WhatsApp Promo)**: मोडासा शोरूम के पते और फोन नंबर के साथ वन-क्लिक शेयर करने योग्य संदेश।
5. **त्योहार शुभकामना संदेश (Festival Greetings)**: दिवाली, नूतन वर्ष, उतरायण, होली, ईद आदि के लिए शुभकामनाएं।
6. **फर्नीचर डिज़ाइन और सुझाव (Design Ideas)**: चारकोल पैनल, फ्लोटिंग टीवी यूनिट, हाइड्रोलिक किचन लेआउट सलाह।
7. **ग्राहक उत्तर सहायक (Customer Reply Assistant)**: मूल्य पूछताछ, गुणवत्ता गारंटी (304 vs 202), और साइट माप शेड्यूलिंग पर तुरंत पेशेवर उत्तर।
8. **डिजिटल कैटलॉग टेक्स्ट (Catalogue Text)**: कैटलॉग के लिए संरचित उत्पाद विनिर्देश।
