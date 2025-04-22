-- Insert main categories (Income, Expenses, etc.)
INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Income', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Expenses', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Savings & Investments', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Leisure & Entertainment', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Debt & Loans', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Gifts & Donations', NULL);

-- Insert subcategories for 'Income'
INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Salary', (SELECT id FROM transaction_categories WHERE name = 'Income')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Bonus', (SELECT id FROM transaction_categories WHERE name = 'Income')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Freelance Work', (SELECT id FROM transaction_categories WHERE name = 'Income')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Investments', (SELECT id FROM transaction_categories WHERE name = 'Income'));

-- Insert subcategories for 'Expenses'
INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Rent', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Utilities', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Groceries', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Transportation', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Health & Fitness', (SELECT id FROM transaction_categories WHERE name = 'Expenses'));

-- Insert subcategories for 'Savings & Investments'
INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Emergency Fund', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Retirement Fund', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Stock Investments', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Real Estate', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments'));

-- Insert subcategories for 'Leisure & Entertainment'
INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Dining Out', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Movies', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Travel', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Hobbies', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment'));

-- Insert subcategories for 'Debt & Loans'
INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Credit Card', (SELECT id FROM transaction_categories WHERE name = 'Debt & Loans')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Personal Loan', (SELECT id FROM transaction_categories WHERE name = 'Debt & Loans')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Mortgage', (SELECT id FROM transaction_categories WHERE name = 'Debt & Loans'));

-- Insert subcategories for 'Gifts & Donations'
INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Charity', (SELECT id FROM transaction_categories WHERE name = 'Gifts & Donations')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Birthday Gifts', (SELECT id FROM transaction_categories WHERE name = 'Gifts & Donations')),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Holiday Gifts', (SELECT id FROM transaction_categories WHERE name = 'Gifts & Donations'));
