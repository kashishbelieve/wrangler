# Zeotap Assignment - Software Engineer Intern

## 👩‍💻 Author
Kashish Ahuja

## 📌 Summary
This project implements unit parsers for ByteSize and TimeDuration, and adds a new directive `aggregate-stats` to compute aggregate statistics using the wrangler DSL.

## 🛠 Key Features
- ByteSize & TimeDuration parsing (e.g. "1.5MB", "300ms")
- New directive `aggregate-stats` for summing, averaging with unit conversions
- Updated `Directives.g4`
- Full unit test coverage

## 📁 File Changes
- `wrangler-api/...` (list files)
- `wrangler-core/...` (list files)
- `Directives.g4` (updated grammar)
- `prompts.txt` (AI prompts used)

## ✅ Build & Test
```bash
mvn clean install
