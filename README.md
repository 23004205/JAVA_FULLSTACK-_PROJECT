SmartExpenseTracker
# Smart Expense Tracker 💰

A full-stack web application developed using **Java, MySQL, HTML, CSS, and JavaScript** to manage personal income and expenses efficiently.

## 📌 Project Overview

Smart Expense Tracker is a personal finance management application that allows users to record income and expenses, organize transactions by category, and monitor their overall financial balance.

The application uses Java for backend processing, JDBC for database connectivity, and MySQL for storing transaction information. The frontend is developed using HTML, CSS, and JavaScript to provide an interactive user interface.

## 🎯 Objectives

- Develop a simple personal finance management system.
- Track daily income and expenses.
- Store transaction details in a MySQL database.
- Calculate total income and expenses automatically.
- Display the remaining account balance.
- Understand full-stack application development.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend development |
| JDBC | Database connectivity |
| MySQL | Database management |
| HTML5 | Frontend structure |
| CSS3 | Styling and responsive design |
| JavaScript | Frontend interactions |
| VS Code | Development environment |
| Git | Version control |
| GitHub | Source code hosting |

## ✨ Features

### 1. Transaction Management
- Add new income and expense transactions.
- Store transaction title, amount, type, category, and date.
- Retrieve transaction records from MySQL.

### 2. Income Tracking
- Record different sources of income.
- Calculate total income using SQL aggregate functions.

### 3. Expense Tracking
- Record daily expenses.
- Categorize expenses such as Education, Food, Transport, and Bills.
- Calculate total expenses.

### 4. Financial Dashboard
- Display total income.
- Display total expenses.
- Calculate remaining balance.
- Present transaction information through the frontend.

### 5. Database Integration
- Connect Java with MySQL using JDBC.
- Use PreparedStatement for database operations.
- Retrieve database records using ResultSet.
- Store financial transaction details persistently.

## 🏗️ Project Architecture

The application follows a frontend-backend-database architecture.

```text
       USER
         |
         v
  HTML + CSS + JavaScript
         |
         v
     Java Backend
         |
         v
        JDBC
         |
         v
     MySQL Database
         |
         v
  Transaction Records
```

### Frontend
HTML, CSS, and JavaScript are responsible for the user interface, transaction forms, and displaying financial information.

### Backend
Java handles application logic, transaction processing, and communication with the database.

### Database
MySQL stores transaction information, including title, amount, transaction type, category, and date.

## 🗄️ Database Structure

**Database Name:** `expense_tracker`

**Table Name:** `transactions`

| Column | Data Type | Description |
|---|---|---|
| id | INT | Primary key, auto increment |
| title | VARCHAR(100) | Transaction title |
| amount | DECIMAL(10,2) | Transaction amount |
| type | VARCHAR(20) | Income or Expense |
| category | VARCHAR(50) | Transaction category |
| transaction_date | DATE | Transaction date |

## ⚙️ Installation and Setup

### Prerequisites

Install the following software before running the project:

1. Java JDK
2. MySQL Server
3. MySQL Workbench (optional)
4. Visual Studio Code
5. Git
6. MySQL Connector/J

### Step 1: Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/SmartExpenseTracker.git
```

### Step 2: Open the Project

Open the downloaded SmartExpenseTracker folder in Visual Studio Code.

### Step 3: Configure MySQL

Open MySQL Workbench and execute:

```sql
CREATE DATABASE expense_tracker;

USE expense_tracker;

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    transaction_date DATE NOT NULL
);
```

### Step 4: Configure Database Connection

Update your database connection settings in the Java database connection file.

```java
String url = "jdbc:mysql://localhost:3306/expense_tracker";
String username = "YOUR_MYSQL_USERNAME";
String password = "YOUR_MYSQL_PASSWORD";
```

Make sure MySQL Connector/J is added to the Java project classpath.

### Step 5: Run the Backend

Run the Java backend/server main class using Visual Studio Code.

Make sure the backend starts successfully and the database connection works.

### Step 6: Run the Frontend

Open the `index.html` file using Live Server or your preferred local development server.

Ensure the Java backend is running and the frontend API URL matches the backend address.

## 📊 Financial Calculations

**Total Income**

```sql
SELECT SUM(amount)
FROM transactions
WHERE type = 'Income';
```

**Total Expenses**

```sql
SELECT SUM(amount)
FROM transactions
WHERE type = 'Expense';
```

**Remaining Balance**

```text
Remaining Balance = Total Income - Total Expenses
```

## 💡 Key Learning Outcomes

- Practical understanding of Java programming and OOP concepts.
- Connecting Java applications with MySQL using JDBC.
- Performing database operations using SQL queries.
- Using PreparedStatement and ResultSet.
- Building frontend interfaces using HTML and CSS.
- Connecting frontend applications with a Java backend.
- Understanding full-stack application architecture.
- Using Git and GitHub for source code management.

## 🚀 Future Enhancements

- User registration and login authentication.
- Monthly and yearly expense reports.
- Interactive financial charts.
- Advanced search and filtering.
- Export transaction reports to PDF or Excel.
- Budget planning and expense alerts.

## 👨‍💻 Developer

**Name:** Singamala Venkata Sai Kumar Reddy

**Project:** Smart Expense Tracker

**Technologies:** Java | JDBC | MySQL | HTML | CSS | JavaScript

## 📄 License

This project is intended for educational and portfolio purposes. See the LICENSE file for licensing terms.
