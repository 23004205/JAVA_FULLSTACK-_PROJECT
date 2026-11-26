
// =============================================
// SMART EXPENSE TRACKER
// ADVANCED JAVASCRIPT DASHBOARD
// Connected to Java API and MySQL
// =============================================


// API CONNECTION

const API_URL = "http://localhost:8080/api/transactions";


// HTML ELEMENTS

const transactionForm = document.getElementById("transactionForm");

const transactionTableBody =
    document.getElementById("transactionTableBody");

const totalIncomeElement =
    document.getElementById("totalIncome");

const totalExpenseElement =
    document.getElementById("totalExpense");

const remainingBalanceElement =
    document.getElementById("remainingBalance");

const searchInput =
    document.getElementById("searchInput");

const typeFilter =
    document.getElementById("typeFilter");

const notification =
    document.getElementById("notification");

const emptyMessage =
    document.getElementById("emptyMessage");

const exportButton =
    document.getElementById("exportButton");


// FORM INPUTS

const titleInput = document.getElementById("title");
const amountInput = document.getElementById("amount");
const typeInput = document.getElementById("type");
const categoryInput = document.getElementById("category");
const dateInput = document.getElementById("date");


// GLOBAL VARIABLES

let transactions = [];

let incomeExpenseChartInstance = null;
let categoryChartInstance = null;
let notificationTimer = null;


// =============================================
// 1. FORMAT CURRENCY
// =============================================

function formatCurrency(amount) {

    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        minimumFractionDigits: 2
    }).format(Number(amount) || 0);

}


// =============================================
// 2. DISPLAY CURRENT DATE
// =============================================

function displayCurrentDate() {

    const currentDateElement =
        document.getElementById("currentDate");

    if (!currentDateElement) return;

    const today = new Date();

    currentDateElement.textContent =
        today.toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric"
        });

}


// =============================================
// 3. SET DEFAULT TRANSACTION DATE
// =============================================

function setDefaultDate() {

    const today = new Date();

    const year = today.getFullYear();

    const month = String(
        today.getMonth() + 1
    ).padStart(2, "0");

    const day = String(
        today.getDate()
    ).padStart(2, "0");

    dateInput.value = `${year}-${month}-${day}`;

}


// =============================================
// 4. SHOW NOTIFICATION
// =============================================

function showNotification(message, isError = false) {

    notification.textContent = message;

    notification.style.background =
        isError ? "#c0392b" : "#173e6b";

    notification.classList.add("show");

    clearTimeout(notificationTimer);

    notificationTimer = setTimeout(() => {

        notification.classList.remove("show");

    }, 3000);

}


// =============================================
// 5. ESCAPE HTML
// Prevent HTML injection in transaction rows
// =============================================

function escapeHTML(value) {

    return String(value ?? "").replace(
        /[&<>"']/g,
        character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[character]
    );

}


// =============================================
// 6. LOAD TRANSACTIONS FROM JAVA API
// =============================================

async function loadTransactions() {

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {

            throw new Error(
                "Unable to load transactions from server."
            );

        }

        transactions = await response.json();

        displayTransactions();

        updateSummary();

        updateCharts();

    } catch (error) {

        console.error("Loading Error:", error);

        showNotification(
            "Could not connect to Java API. Check whether ApiServer is running.",
            true
        );

    }

}


// =============================================
// 7. SEARCH AND FILTER TRANSACTIONS
// =============================================

function getFilteredTransactions() {

    const searchText =
        searchInput.value.trim().toLowerCase();

    const selectedType =
        typeFilter.value;

    return transactions.filter(transaction => {

        const title =
            String(transaction.title || "").toLowerCase();

        const category =
            String(transaction.category || "").toLowerCase();

        const matchesSearch =
            title.includes(searchText) ||
            category.includes(searchText);

        const matchesType =
            selectedType === "All" ||
            transaction.type === selectedType;

        return matchesSearch && matchesType;

    });

}


// =============================================
// 8. DISPLAY TRANSACTIONS IN TABLE
// =============================================

function displayTransactions() {

    const filteredTransactions =
        getFilteredTransactions();

    transactionTableBody.innerHTML = "";

    if (filteredTransactions.length === 0) {

        emptyMessage.style.display = "block";

        emptyMessage.textContent =
            transactions.length === 0
                ? "No transactions available. Add your first transaction!"
                : "No matching transactions found.";

        return;

    }

    emptyMessage.style.display = "none";

    filteredTransactions.forEach(transaction => {

        const row = document.createElement("tr");

        const badgeClass =
            transaction.type === "Income"
                ? "income-badge"
                : "expense-badge";

        const formattedDate =
            escapeHTML(transaction.date);

        row.innerHTML = `

            <td>${escapeHTML(transaction.id)}</td>

            <td>${escapeHTML(transaction.title)}</td>

            <td>${formatCurrency(transaction.amount)}</td>

            <td>
                <span class="type-badge ${badgeClass}">
                    ${escapeHTML(transaction.type)}
                </span>
            </td>

            <td>${escapeHTML(transaction.category)}</td>

            <td>${formattedDate}</td>

            <td>

                <button
                    class="action-button edit-button"
                    onclick="editTransaction(${Number(transaction.id)})">
                    Edit
                </button>

                <button
                    class="action-button delete-button"
                    onclick="deleteTransaction(${Number(transaction.id)})">
                    Delete
                </button>

            </td>

        `;

        transactionTableBody.appendChild(row);

    });

}


// =============================================
// 9. UPDATE FINANCIAL SUMMARY
// =============================================

function updateSummary() {

    let totalIncome = 0;

    let totalExpense = 0;

    transactions.forEach(transaction => {

        const amount = Number(transaction.amount) || 0;

        if (transaction.type === "Income") {

            totalIncome += amount;

        } else if (transaction.type === "Expense") {

            totalExpense += amount;

        }

    });

    const remainingBalance =
        totalIncome - totalExpense;

    totalIncomeElement.textContent =
        formatCurrency(totalIncome);

    totalExpenseElement.textContent =
        formatCurrency(totalExpense);

    remainingBalanceElement.textContent =
        formatCurrency(remainingBalance);

}


// =============================================
// 10. CREATE FORM DATA
// =============================================

function createFormData(data) {

    return new URLSearchParams({

        title: data.title,

        amount: data.amount,

        type: data.type,

        category: data.category,

        date: data.date

    });

}


// =============================================
// 11. ADD NEW TRANSACTION
// =============================================

transactionForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const transaction = {

        title: titleInput.value.trim(),

        amount: Number(amountInput.value),

        type: typeInput.value,

        category: categoryInput.value.trim(),

        date: dateInput.value

    };


    // VALIDATION

    if (!transaction.title || !transaction.category) {

        showNotification(
            "Please enter the title and category.",
            true
        );

        return;

    }

    if (!Number.isFinite(transaction.amount) ||
        transaction.amount <= 0) {

        showNotification(
            "Please enter a valid amount.",
            true
        );

        return;

    }

    if (!transaction.date) {

        showNotification(
            "Please select a transaction date.",
            true
        );

        return;

    }


    try {

        const response = await fetch(API_URL, {

            method: "POST",

            headers: {
                "Content-Type":
                    "application/x-www-form-urlencoded"
            },

            body: createFormData(transaction)

        });

        if (!response.ok) {

            throw new Error("Failed to add transaction.");

        }

        transactionForm.reset();

        setDefaultDate();

        await loadTransactions();

        showNotification(
            "Transaction added successfully!"
        );

    } catch (error) {

        console.error("Add Error:", error);

        showNotification(
            "Unable to add transaction.",
            true
        );

    }

});


// =============================================
// 12. EDIT TRANSACTION
// =============================================

async function editTransaction(id) {

    const transaction =
        transactions.find(item => Number(item.id) === Number(id));

    if (!transaction) {

        showNotification(
            "Transaction not found.",
            true
        );

        return;

    }


    const updatedTitle = prompt(
        "Enter transaction title:",
        transaction.title
    );

    if (updatedTitle === null) return;


    const updatedAmount = prompt(
        "Enter transaction amount:",
        transaction.amount
    );

    if (updatedAmount === null) return;


    const updatedType = prompt(
        "Enter type: Income or Expense",
        transaction.type
    );

    if (updatedType === null) return;


    const updatedCategory = prompt(
        "Enter category:",
        transaction.category
    );

    if (updatedCategory === null) return;


    const updatedDate = prompt(
        "Enter date (YYYY-MM-DD):",
        transaction.date
    );

    if (updatedDate === null) return;


    // VALIDATION

    const amount = Number(updatedAmount);

    const normalizedType =
        updatedType.trim().toLowerCase();

    if (!updatedTitle.trim() ||
        !updatedCategory.trim()) {

        showNotification(
            "Title and category cannot be empty.",
            true
        );

        return;

    }

    if (!Number.isFinite(amount) || amount <= 0) {

        showNotification(
            "Enter a valid amount.",
            true
        );

        return;

    }

    if (
        normalizedType !== "income" &&
        normalizedType !== "expense"
    ) {

        showNotification(
            "Type must be Income or Expense.",
            true
        );

        return;

    }

    if (!/^\d{4}-\d{2}-\d{2}$/.test(updatedDate.trim())) {

        showNotification(
            "Enter the date in YYYY-MM-DD format.",
            true
        );

        return;

    }


    const updatedTransaction = {

        title: updatedTitle.trim(),

        amount: amount,

        type:
            normalizedType === "income"
                ? "Income"
                : "Expense",

        category: updatedCategory.trim(),

        date: updatedDate.trim()

    };


    try {

        const response = await fetch(
            `${API_URL}/${id}`,
            {

                method: "PUT",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },

                body: createFormData(updatedTransaction)

            }
        );

        if (!response.ok) {

            throw new Error("Failed to update transaction.");

        }

        await loadTransactions();

        showNotification(
            "Transaction updated successfully!"
        );

    } catch (error) {

        console.error("Update Error:", error);

        showNotification(
            "Unable to update transaction.",
            true
        );

    }

}


// =============================================
// 13. DELETE TRANSACTION
// =============================================

async function deleteTransaction(id) {

    const confirmDelete = confirm(
        "Are you sure you want to delete this transaction?"
    );

    if (!confirmDelete) return;


    try {

        const response = await fetch(
            `${API_URL}/${id}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {

            throw new Error("Failed to delete transaction.");

        }

        await loadTransactions();

        showNotification(
            "Transaction deleted successfully!"
        );

    } catch (error) {

        console.error("Delete Error:", error);

        showNotification(
            "Unable to delete transaction.",
            true
        );

    }

}


// =============================================
// 14. INCOME VS EXPENSE CHART
// =============================================

function updateIncomeExpenseChart() {

    const chartElement =
        document.getElementById("incomeExpenseChart");

    if (!chartElement || typeof Chart === "undefined") {
        return;
    }

    let totalIncome = 0;

    let totalExpense = 0;

    transactions.forEach(transaction => {

        const amount = Number(transaction.amount) || 0;

        if (transaction.type === "Income") {

            totalIncome += amount;

        } else if (transaction.type === "Expense") {

            totalExpense += amount;

        }

    });


    if (incomeExpenseChartInstance) {

        incomeExpenseChartInstance.destroy();

    }


    incomeExpenseChartInstance = new Chart(
        chartElement,
        {

            type: "bar",

            data: {

                labels: ["Income", "Expenses"],

                datasets: [{

                    label: "Amount (₹)",

                    data: [
                        totalIncome,
                        totalExpense
                    ],

                    backgroundColor: [
                        "#20b879",
                        "#ed6470"
                    ],

                    borderRadius: 8,

                    barThickness: 65

                }]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                plugins: {

                    legend: {
                        display: false
                    }

                },

                scales: {

                    y: {

                        beginAtZero: true,

                        ticks: {
                            callback: value => "₹" + value
                        }

                    }

                }

            }

        }
    );

}


// =============================================
// 15. EXPENSE BY CATEGORY CHART
// =============================================

function updateCategoryChart() {

    const chartElement =
        document.getElementById("categoryChart");

    if (!chartElement || typeof Chart === "undefined") {
        return;
    }

    const categoryTotals = {};

    transactions.forEach(transaction => {

        if (transaction.type === "Expense") {

            const category =
                transaction.category || "Uncategorized";

            const amount =
                Number(transaction.amount) || 0;

            categoryTotals[category] =
                (categoryTotals[category] || 0) + amount;

        }

    });


    const labels = Object.keys(categoryTotals);

    const values = Object.values(categoryTotals);


    if (categoryChartInstance) {

        categoryChartInstance.destroy();

    }


    categoryChartInstance = new Chart(
        chartElement,
        {

            type: "doughnut",

            data: {

                labels: labels.length
                    ? labels
                    : ["No expenses yet"],

                datasets: [{

                    data: values.length
                        ? values
                        : [1],

                    backgroundColor: [

                        "#398ee8",
                        "#20b879",
                        "#ed6470",
                        "#f4b942",
                        "#9868e8",
                        "#26b6b0",
                        "#f18b45"

                    ],

                    borderWidth: 2,

                    borderColor: "#ffffff"

                }]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                plugins: {

                    legend: {

                        position: "bottom",

                        labels: {
                            padding: 15
                        }

                    },

                    tooltip: {

                        callbacks: {

                            label: function(context) {

                                if (!values.length) {
                                    return "No expenses yet";
                                }

                                return context.label + ": " +
                                    formatCurrency(context.raw);

                            }

                        }

                    }

                }

            }

        }
    );

}


// =============================================
// 16. UPDATE ALL CHARTS
// =============================================

function updateCharts() {

    updateIncomeExpenseChart();

    updateCategoryChart();

}


// =============================================
// 17. EXPORT TRANSACTIONS TO CSV
// =============================================

function exportTransactions() {

    if (transactions.length === 0) {

        showNotification(
            "There are no transactions to export.",
            true
        );

        return;

    }


    const headers = [

        "ID",
        "Title",
        "Amount",
        "Type",
        "Category",
        "Date"

    ];


    const rows = transactions.map(transaction => [

        transaction.id,

        transaction.title,

        transaction.amount,

        transaction.type,

        transaction.category,

        transaction.date

    ]);


    // Escape values containing commas or quotes

    function escapeCSV(value) {

        const text = String(value ?? "");

        return `"${text.replace(/"/g, '""')}"`;

    }


    const csvContent = [

        headers,

        ...rows

    ].map(row =>

        row.map(escapeCSV).join(",")

    ).join("\r\n");


    // BOM helps Excel display text correctly

    const blob = new Blob(

        ["\uFEFF" + csvContent],

        {
            type: "text/csv;charset=utf-8;"
        }

    );


    const url = URL.createObjectURL(blob);

    const downloadLink = document.createElement("a");

    downloadLink.href = url;

    downloadLink.download = "Smart_Expense_Tracker.csv";

    document.body.appendChild(downloadLink);

    downloadLink.click();

    document.body.removeChild(downloadLink);

    URL.revokeObjectURL(url);


    showNotification(
        "CSV file exported successfully!"
    );

}


// =============================================
// 18. SEARCH EVENT
// =============================================

searchInput.addEventListener("input", function() {

    displayTransactions();

});


// =============================================
// 19. FILTER EVENT
// =============================================

typeFilter.addEventListener("change", function() {

    displayTransactions();

});


// =============================================
// 20. EXPORT BUTTON EVENT
// =============================================

exportButton.addEventListener("click", function() {

    exportTransactions();

});


// =============================================
// 21. NAVIGATION ACTIVE STATE
// =============================================

const navigationLinks =
    document.querySelectorAll(".nav-link");

navigationLinks.forEach(link => {

    link.addEventListener("click", function() {

        navigationLinks.forEach(navLink => {

            navLink.classList.remove("active");

        });

        this.classList.add("active");

    });

});


// =============================================
// 22. START APPLICATION
// =============================================

displayCurrentDate();

setDefaultDate();

loadTransactions();

