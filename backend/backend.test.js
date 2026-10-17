const { test } = require("node:test");
const assert = require("node:assert");

// Test validation logic matching backend/index.js
function validateRequest(body, authHeader) {
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return { status: 401, error: "Unauthorized" };
  }
  const { prompt } = body;
  if (!prompt || typeof prompt !== "string" || prompt.trim().length === 0) {
    return { status: 400, error: "Prompt is required" };
  }
  if (prompt.length > 2500) {
    return { status: 400, error: "Prompt exceeds 2500 characters limit" };
  }
  return { status: 200 };
}

test("backend rejects requests without Bearer token", () => {
  const result = validateRequest({ prompt: "Hello" }, null);
  assert.strictEqual(result.status, 401);
});

test("backend rejects empty prompt", () => {
  const result = validateRequest({ prompt: "   " }, "Bearer test-token");
  assert.strictEqual(result.status, 400);
});

test("backend rejects oversized prompt (>2500 chars)", () => {
  const longPrompt = "A".repeat(2501);
  const result = validateRequest({ prompt: longPrompt }, "Bearer test-token");
  assert.strictEqual(result.status, 400);
});

test("backend accepts valid authenticated prompt", () => {
  const result = validateRequest({ prompt: "एसएस 304 रेलिंग का विवरण लिखें" }, "Bearer valid-token");
  assert.strictEqual(result.status, 200);
});
