const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read customers", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("customers").get());
});

test("Authenticated user: can create own profile and read own customer", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Harish Patel",
      role: "admin",
      businessName: "Aravalli Steel PVC Furniture"
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("customers").doc("cust_1").set({
      userId: ALICE_UID,
      name: "Ramesh Bhai",
      phone: "+91 98980 11223"
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("customers").doc("cust_1").get()
  );
});

test("Authenticated user: cannot read or write another user's customer", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("customers").doc("bob_cust").set({
      userId: BOB_UID,
      name: "Bob Customer",
      phone: "1234567890"
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("customers").doc("bob_cust").get()
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("customers").doc("new_cust").set({
      userId: BOB_UID,
      name: "Hacked",
      phone: "0000000000"
    })
  );
});
