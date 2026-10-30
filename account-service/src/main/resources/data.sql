-- Seed Accounts
INSERT INTO accounts (customer_name, balance, owner_username, created_at) VALUES ('John Doe', 5000.00, 'john_doe', CURRENT_TIMESTAMP);
INSERT INTO accounts (customer_name, balance, owner_username, created_at) VALUES ('Alice Smith', 10000.00, 'alice_smith', CURRENT_TIMESTAMP);
INSERT INTO accounts (customer_name, balance, owner_username, created_at) VALUES ('Bob Johnson', 7500.00, 'bob_johnson', CURRENT_TIMESTAMP);

-- Seed Employees for 2nd Highest Salary query demo
INSERT INTO employees (name, department, salary) VALUES ('Emp A', 'Engineering', 90000.00);
INSERT INTO employees (name, department, salary) VALUES ('Emp B', 'Engineering', 120000.00);
INSERT INTO employees (name, department, salary) VALUES ('Emp C', 'Finance', 105000.00);
INSERT INTO employees (name, department, salary) VALUES ('Emp D', 'HR', 80000.00);
INSERT INTO employees (name, department, salary) VALUES ('Emp E', 'Finance', 110000.00);

-- Seed Payment Logs for Top-3 Customers window function query demo
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('John Doe', 1500.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('John Doe', 2500.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('Alice Smith', 5000.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('Alice Smith', 3000.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('Bob Johnson', 1000.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('Bob Johnson', 2000.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('Charlie Brown', 500.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('Charlie Brown', 700.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('David Clark', 400.00, CURRENT_TIMESTAMP);
INSERT INTO payment_log (customer_name, amount, created_at) VALUES ('Eva Green', 300.00, CURRENT_TIMESTAMP);
