# 🛡️ Intrusion Detection System

A Spring Boot based Intrusion Detection System (IDS) designed to monitor, detect, and prevent suspicious activities in a distributed system environment.

---

## 🚀 Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- Redis
- JWT Authentication
- Docker
- Maven
- REST APIs

---

## 📌 Features

- 🔐 Secure Authentication using JWT
- 🚦 Request Monitoring & Rate Limiting
- 📊 Suspicious Activity Detection
- 🧠 IP-based Tracking
- ⚡ Redis for fast caching & threshold detection
- 🐳 Dockerized Deployment
- 📝 Centralized Logging

---

## 🏗️ Architecture

The system monitors incoming API requests and:

1. Tracks request frequency
2. Identifies abnormal behavior
3. Flags suspicious IP addresses
4. Stores temporary detection data in Redis
5. Blocks malicious requests based on configured thresholds

---

## 📂 Architecture Diagram
![Architecture Diagram](https://github.com/whoa12/Intrusion-Detection/blob/main/Architecture_Diagram.png?raw=true)
