[English](README.md) | 中文

<div align="center">

  <a href="https://daacoo.com"><picture>
    <source media="(prefers-color-scheme: dark)" srcset="assets/darkelato.png">
    <source media="(prefers-color-scheme: light)" srcset="assets/lightelato.png">
    <img alt="elato logo" src="assets/lightelato.png" height="70" style="max-width: 100%;">
  </picture></a>
  
<div style="display:flex; flex-direction:row; align-items:center; flex-wrap:wrap; justify-content:center;">
  <a style="display:inline-flex;" href="https://cookbook.openai.com/examples/voice_solutions/running_realtime_api_speech_on_esp32_arduino_edge_runtime_daacoo"><img src="assets/oai.png" height="42" style="width: auto;"></a>  
  <a style="display:inline-flex;" href="https://www.daacoo.com/docs"><img src="assets/docs.png" height="42" style="width: auto;"></a>
  <a style="display:inline-flex;" href="https://discord.gg/KJWxDPBRUj"><img src="assets/discord.png" height="42" style="width: auto;"></a>
      <a style="display:inline-flex;" href="https://www.kickstarter.com/projects/daacoo/elato-make-toys-talk-with-ai-voices"><img src="assets/ks.png" height="42" style="width: auto;"></a>
</div>
<a href="https://www.kickstarter.com/projects/daacoo/elato-make-toys-talk-with-ai-voices" target="_blank">
 <img src="assets/cover.png" alt="Elato Logo" width="100%">
</a>

<br />
</div>



# 🤖 答酷 DaaCoo AI — ESP32 上的实时语音 AI 生态

这是一个**非常完整**的边缘语音 AI 项目，我来帮你梳理一下核心亮点：

---

## 🎯 一句话总结

> **用一块 ESP32（不到 ¥20 的芯片）+ WiFi，就能拥有一个接入 OpenAI/Gemini/Grok/ElevenLabs/Hume 的全球语音 AI 玩具/设备，端到端延迟 < 2 秒。**

---

## 📐 架构三层解读

| 层级 | 技术 | 职责 |
|------|------|------|
| 🖥️ **前端** | Next.js (Vercel) | 创建 AI 智能体、对话历史、音量/音高控制、设备管理 |
| ⚡ **边缘** | Deno Edge / Cloudflare Workers | WebSocket 桥接 + LLM/TTS/STT 调用 + VAD 轮次检测 |
| 📟 **设备端** | ESP32-S3 (PlatformIO) | 录音 → Opus 编码 → WebSocket 上传 → 播放回复 |

```
📱 手机/网页  ←WebRTC/WS→  ☁️ Deno/CF Edge  ←WSS→  📟 ESP32
   Next.js                    LLM+TTS+STT              音频I/O
```

---

## 🔥 最值得关注的 6 个点

| # | 亮点 | 为什么重要 |
|---|------|-----------|
| 1️⃣ | **无需 PSRAM** | ESP32 通常需要外接 PSRAM 才能跑 AI，这里优化到不需要 → 成本极低 |
| 2️⃣ | **100+ 模型支持** | 不绑定单一 API，OpenAI/Gemini/Grok/ElevenLabs/Hume 随便切 |
| 3️⃣ | **Opus 12kbps** | 极低带宽下保持高质量语音，适合全球部署 |
| 4️⃣ | **20 分钟不间断对话** | WebSocket 长连接 + 边缘函数，远超一般 IoT 对话时长 |
| 5️⃣ | **全球边缘部署** | Deno Edge / Cloudflare Workers 保证低延迟，不是集中在一个机房 |
| 6️⃣ | **完整产品化** | 不是 Demo，有 OTA 更新、Captive Portal 配网、恢复出厂、用户认证… |

---

## 📊 关键指标一览

| 指标 | 数值 |
|------|------|
| 🌍 全球延迟 | **< 2 秒**（往返） |
| 🎙️ 音频编码 | Opus @ **12 kbps** / 24kHz |
| ⏱️ 最长对话 | **~17-20 分钟**连续 |
| 🔌 冷启动 | 3-4 秒 |
| 💰 硬件成本 | ESP32-S3 ≈ **¥15-25** |

---

## ⚠️ 当前限制（诚实说）

| 限制 | 说明 |
|------|------|
| 🚫 语音打断 | ESP32 端暂不支持（OpenAI 端已支持） |
| ⏰ Wall clock 限制 | 边缘函数有超时，超过时间会断 |
| 🧊 冷启动 | 首次连接 3-4 秒延迟 |
| 📡 依赖云端 LLM | 断网就不能用（当然本地 MLX 方案可选） |

---

## 🆚 对比其他方案

| 方案 | 硬件 | 延迟 | 成本 | 离线能力 |
|------|------|------|------|----------|
| **DaaCoo AI** | ESP32-S3 | <2s | ¥20 + API | ❌ |
| Raspberry Pi + Local LLM | RPi 4/5 | 1-3s | ¥200+ | ✅ |
| Home Assistant Voice | 任意 | 2-5s | 免费 | ✅ |
| Cloudflare Voice Agents | 无硬件 | <1s | 按量付费 | ❌ |

---

## 💡 我的判断

这个项目的**真正价值**不在于技术本身（语音 AI Pipeline 已经有很多了），而在于：

> **它把"云端顶级语音 AI"和"边缘极简硬件"之间的鸿沟，用一套开箱即用的方案填平了。**

对于想做：
- 🎮 **AI 玩具/机器人**
- 🗣️ **语音交互硬件产品**
- 🏠 **智能家居语音节点**
- 🎓 **边缘 AI 教育项目**

这几乎是目前**门槛最低、功能最全**的方案之一。

---

**⭐ GitHub Star 走起！** 这个项目如果持续维护，有可能成为 ESP32 语音 AI 的"标准答案"。
