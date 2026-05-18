# 🧠 NeuroChat – AI-Powered Chat Backend 🚀

> A production-ready AI chatbot backend built with Spring Boot, featuring secure authentication, scalable rate limiting, AI integration, and real-world payment systems.

---

## 🌐 Live Demo  
🔗 https://neurochat-amxn.onrender.com/chat.html  

---

## 📌 Introduction  

NeuroChat is a **scalable backend system for AI-driven conversations**, designed to simulate real-world SaaS platforms like ChatGPT.  

It combines **secure authentication, AI API integration, rate limiting, and monetization**, making it a complete backend solution for modern AI applications.

---

## ✨ Features  

- 🔐 **JWT Authentication** – Secure, stateless user authentication  
- 🤖 **AI Chat Integration** – Real-time responses using LLM APIs (OpenRouter)  
- ⚡ **Rate Limiting (Redis)** – Prevents API abuse and ensures fair usage  
- 💳 **Payment Integration** – Razorpay for premium features  
- 💾 **Data Persistence** – MySQL with JPA/Hibernate  
- 🐳 **Dockerized Deployment** – Container-ready application  
- 🔄 **CI/CD Pipeline** – Automated build & deployment using GitHub Actions  
- ☁️ **Cloud Deployment** – Hosted on Render  

---

## 🛠️ Tech Stack  

| Category        | Technology |
|----------------|-----------|
| Backend        | Java 17, Spring Boot |
| Security       | Spring Security, JWT |
| Database       | MySQL, Hibernate (JPA) |
| Caching        | Redis |
| AI Integration | OpenRouter API (GPT models) |
| Payments       | Razorpay |
| DevOps         | Docker, GitHub Actions, Render |
| Build Tool     | Maven |

---

## 🚀 Installation & Setup  

### 🔧 Prerequisites
- Java 17+
- Maven
- MySQL
- Redis (optional but recommended)

---

### 📥 Clone Repository
```bash
git clone https://github.com/your-username/neurochat.git
cd neurochat

⚙️ Configure Environment Variables

Create/update application.properties:

OPENROUTER_API_KEY=your_api_key
RAZORPAY_KEY=your_key
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_pass


▶️ Run Application
mvn clean install
mvn spring-boot:run


🧪 Usage
Open the live demo or local frontend
Register/Login to get JWT token
Send chat prompts
Receive AI-generated responses
Upgrade via payment for premium usage


📂 Project Structure
neurochat/
 ├── controller/        # REST API endpoints
 ├── service/           # Business logic & AI integration
 ├── repository/        # Database access layer
 ├── entity/            # Data models
 ├── config/            # Configurations
 ├── security/          # JWT & filters
 ├── resources/
 │    ├── static/       # Frontend (chat.html)
 │    ├── templates/
 │    └── application.properties



 ## 🖼️ Screenshots

### 💬 Chat UI
![Chat UI](neurochat/docs/chat-ui.png)


🔄 CI/CD Pipeline

This project uses GitHub Actions for automation.

Workflow:

Code Push → Build (Maven) → Dockerize → Deploy (Render)

✔ Automated builds
✔ Consistent deployments
✔ DevOps-ready architecture


🚀 Future Improvements
🔌 WebSocket-based real-time chat
📊 Monitoring (Prometheus + Grafana)
📘 Swagger/OpenAPI documentation
⚡ AI response caching (Redis)
🧪 Unit & integration testing
📦 Microservices architecture
🤝 Contributing

Contributions are welcome!

Fork the repository
Create a feature branch
Commit your changes
Push and open a Pull Request
📜 License

This project is licensed under the MIT License.

👨‍💻 Author

Adityaraj
Backend Developer | Java | Spring Boot | AI Systems