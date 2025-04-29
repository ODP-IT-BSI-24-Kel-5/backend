-- Insert main categories (Income, Expenses, etc.)
INSERT INTO transaction_categories ( created_at, updated_at, name, icon, parent_id)
VALUES
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Income', 'banknote-arrow-up', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Expenses', 'banknote-arrow-down', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Savings', 'piggy-bank', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Investments', 'trending-up', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Entertainment', 'ferris-wheel', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Leisure', 'tree-palm', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Debt', 'circle-dollar-sign',NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Loan', 'receipt',NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Gifts','gift', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Donations','hand-coins', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Salary','banknote', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Transportation','bus', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Rent','house', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Food','utensils', NULL),
  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Groceries','shopping-cart', NULL);

---- Insert subcategories for 'Income'
--INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
--VALUES
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Salary', (SELECT id FROM transaction_categories WHERE name = 'Income')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Bonus', (SELECT id FROM transaction_categories WHERE name = 'Income')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Freelance Work', (SELECT id FROM transaction_categories WHERE name = 'Income')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Investments', (SELECT id FROM transaction_categories WHERE name = 'Income'));
--
---- Insert subcategories for 'Expenses'
--INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
--VALUES
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Rent', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Utilities', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Groceries', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Transportation', (SELECT id FROM transaction_categories WHERE name = 'Expenses')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Health & Fitness', (SELECT id FROM transaction_categories WHERE name = 'Expenses'));
--
---- Insert subcategories for 'Savings & Investments'
--INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
--VALUES
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Emergency Fund', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Retirement Fund', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Stock Investments', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Real Estate', (SELECT id FROM transaction_categories WHERE name = 'Savings & Investments'));
--
---- Insert subcategories for 'Leisure & Entertainment'
--INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
--VALUES
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Dining Out', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Movies', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Travel', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Hobbies', (SELECT id FROM transaction_categories WHERE name = 'Leisure & Entertainment'));
--
---- Insert subcategories for 'Debt & Loans'
--INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
--VALUES
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Credit Card', (SELECT id FROM transaction_categories WHERE name = 'Debt & Loans')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Personal Loan', (SELECT id FROM transaction_categories WHERE name = 'Debt & Loans')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Mortgage', (SELECT id FROM transaction_categories WHERE name = 'Debt & Loans'));
--
---- Insert subcategories for 'Gifts & Donations'
--INSERT INTO transaction_categories ( created_at, updated_at, name, parent_id)
--VALUES
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Charity', (SELECT id FROM transaction_categories WHERE name = 'Gifts & Donations')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Birthday Gifts', (SELECT id FROM transaction_categories WHERE name = 'Gifts & Donations')),
--  ('2025-04-19T18:03:18.928692', '2025-04-19T18:03:18.928692', 'Holiday Gifts', (SELECT id FROM transaction_categories WHERE name = 'Gifts & Donations'));
