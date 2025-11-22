-- Intent to Category Mappings
INSERT INTO intent_category_mapping (intent, primary_category, secondary_categories, rank_order) VALUES
('TRAVEL', 'CREDIT_CARD', ARRAY['INSURANCE', 'SAVINGS'], 1),
('LOAN', 'FINANCING', ARRAY['CREDIT_CARD'], 1),
('SAVINGS', 'CASA', ARRAY['INVESTMENT'], 1),
('INVESTMENT', 'INVESTMENT', ARRAY['SAVINGS'], 1),
('CAR', 'FINANCING', ARRAY['INSURANCE'], 1),
('HOME', 'FINANCING', ARRAY['INSURANCE', 'CASA'], 1),
('EDUCATION', 'FINANCING', ARRAY['SAVINGS'], 1),
('INSURANCE', 'INSURANCE', ARRAY[]::VARCHAR[], 1),
('BUSINESS', 'FINANCING', ARRAY['CASA', 'CREDIT_CARD'], 1),
('PAYMENT', 'CREDIT_CARD', ARRAY['CASA'], 1),
('GENERAL', 'CASA', ARRAY['CREDIT_CARD', 'INVESTMENT'], 1);

-- Islamic Banking Products (30 products)
INSERT INTO products (product_code, product_name, category, sub_category, description, islamic_structure, annual_rate, annual_fee, min_income, min_credit_score, eligibility_criteria, key_benefits) VALUES

-- Credit Cards (5 products)
('CC_TRAVEL_01', 'Voyager Travel Credit Card', 'CREDIT_CARD', 'TRAVEL', 'Premium travel rewards credit card with airport benefits', 'Murabaha', NULL, 150.00, 50000, 650, '{"age_min": 21, "age_max": 65, "employment": "salaried or self-employed"}', '["5% cashback on international travel", "No foreign transaction fees", "Airport lounge access worldwide", "Travel insurance up to $100,000", "Concierge service"]'),
('CC_CASHBACK_01', 'CashPlus Rewards Card', 'CREDIT_CARD', 'CASHBACK', 'Everyday cashback credit card for all purchases', 'Murabaha', NULL, 100.00, 30000, 600, '{"age_min": 21, "age_max": 65}', '["2% cashback on all purchases", "No annual fee first year", "Instant cashback redemption", "Supplementary card free"]'),
('CC_BUSINESS_01', 'Business Elite Card', 'CREDIT_CARD', 'BUSINESS', 'Corporate credit card with expense management features', 'Murabaha', NULL, 250.00, 75000, 700, '{"business_age": 2, "annual_turnover": 500000}', '["Expense management dashboard", "Higher credit limit up to 500,000", "Business rewards program", "Free supplementary cards for employees", "Quarterly business reports"]'),
('CC_STUDENT_01', 'Campus Card', 'CREDIT_CARD', 'STUDENT', 'Student credit card with education benefits', 'Murabaha', NULL, 0, NULL, NULL, '{"student": true, "age_min": 18}', '["No annual fee", "Cashback on education expenses", "Build credit history", "Financial literacy resources"]'),
('CC_PLATINUM_01', 'Platinum Privileges Card', 'CREDIT_CARD', 'PREMIUM', 'Exclusive card with luxury benefits', 'Murabaha', NULL, 500.00, 150000, 750, '{"age_min": 25}', '["Unlimited lounge access", "Golf privileges", "Dining benefits", "Personal relationship manager", "Higher reward points"]'),

-- Financing Products (8 products)
('FIN_AUTO_01', 'DriveEasy Auto Finance', 'FINANCING', 'AUTO', 'Flexible car financing with competitive rates', 'Ijara', 4.5, 0, 30000, 600, '{"age_min": 21, "employment_months": 6}', '["0% down payment option", "Fixed profit rate", "Up to 7 years tenure", "Insurance bundled", "Fast approval in 24 hours"]'),
('FIN_AUTO_LUX', 'Luxury Auto Finance', 'FINANCING', 'AUTO', 'Premium vehicle financing for luxury cars', 'Musharakah', 3.9, 0, 100000, 700, '{"age_min": 25}', '["Competitive rates for luxury vehicles", "Up to 5 years tenure", "Takaful insurance included", "Dedicated relationship manager"]'),
('FIN_HOME_01', 'HomeDream Finance', 'FINANCING', 'HOME', 'Comprehensive home financing solution', 'Diminishing Musharakah', 3.5, 500.00, 75000, 650, '{"age_min": 21, "age_max": 65}', '["Up to 85% financing", "25 years maximum tenure", "No hidden charges", "Free property valuation", "Life takaful coverage"]'),
('FIN_HOME_FIRST', 'First Home Advantage', 'FINANCING', 'HOME', 'Special financing for first-time buyers', 'Ijara', 3.2, 300.00, 50000, 620, '{"first_time_buyer": true}', '["Up to 90% financing", "Reduced profit rate", "Government subsidy eligible", "Flexible payment options", "Grace period available"]'),
('FIN_PERSONAL_01', 'QuickCash Personal Finance', 'FINANCING', 'PERSONAL', 'Fast personal financing for various needs', 'Murabaha', 5.5, 0, 25000, 580, '{"age_min": 21, "employment_months": 3}', '["Quick approval in 4 hours", "No collateral required", "Flexible tenure up to 5 years", "No prepayment penalty", "Salary transfer not required"]'),
('FIN_EDUCATION_01', 'EduFund Education Finance', 'FINANCING', 'EDUCATION', 'Education financing for higher studies', 'Murabaha', 3.0, 0, 40000, 600, '{"age_min": 21}', '["Deferred payment during study", "Cover tuition and living expenses", "Grace period of 6 months", "Up to 10 years repayment", "No collateral for amount under 200,000"]'),
('FIN_BUSINESS_01', 'BizGrow SME Finance', 'FINANCING', 'BUSINESS', 'Business expansion financing for SMEs', 'Musharakah', 4.0, 0, NULL, NULL, '{"business_age": 2, "profitable": true}', '["Working capital financing", "Equipment purchase", "Up to 5 million financing", "Flexible repayment", "Business advisory services"]'),
('FIN_RENOVATION', 'HomeImprove Renovation Finance', 'FINANCING', 'HOME', 'Home renovation and improvement financing', 'Murabaha', 4.8, 0, 40000, 600, '{"property_owned": true}', '["Up to 300,000 financing", "No mortgage required", "Quick approval", "Contractor network access"]'),

-- CASA Products (5 products)
('CASA_SAV_01', 'MaxProfit Savings Account', 'CASA', 'SAVINGS', 'High-yield savings account with monthly profits', 'Mudarabah', 2.5, 0, NULL, NULL, '{"age_min": 18}', '["Monthly profit distribution", "No minimum balance", "Free debit card", "Online and mobile banking", "24/7 customer service"]'),
('CASA_SAV_PREMIUM', 'Elite Savings Account', 'CASA', 'SAVINGS', 'Premium savings with exclusive benefits', 'Mudarabah', 3.5, 100.00, 100000, NULL, '{"age_min": 21}', '["Higher profit rates", "Premium banking services", "Wealth management advisory", "Priority banking", "Free international transfers"]'),
('CASA_CURRENT_01', 'ActiveCurrent Account', 'CASA', 'CURRENT', 'Full-featured current account for daily banking', 'Qard', NULL, 0, NULL, NULL, '{"age_min": 18}', '["Free checkbook", "Unlimited transactions", "Online banking", "Bill payment services", "SMS alerts"]'),
('CASA_YOUTH_01', 'YoungSavers Account', 'CASA', 'YOUTH', 'Savings account designed for youth', 'Mudarabah', 3.0, 0, NULL, NULL, '{"age_min": 13, "age_max": 25}', '["Special youth profit rate", "Education rewards program", "Parental controls available", "Financial literacy workshops", "University benefits"]'),
('CASA_SENIOR_01', 'GoldenYears Account', 'CASA', 'SENIOR', 'Special account for senior citizens', 'Mudarabah', 4.0, 0, NULL, NULL, '{"age_min": 55}', '["Highest profit rate", "Health insurance discounts", "Priority service", "No minimum balance", "Estate planning services"]'),

-- Insurance/Takaful Products (6 products)
('INS_TRAVEL_01', 'SafeJourney Travel Takaful', 'INSURANCE', 'TRAVEL', 'Comprehensive travel protection worldwide', 'Takaful', NULL, 200.00, NULL, NULL, '{}', '["Worldwide coverage", "Medical emergencies up to $500,000", "Trip cancellation coverage", "Lost baggage compensation", "24/7 assistance helpline"]'),
('INS_AUTO_01', 'AutoGuard Motor Takaful', 'INSURANCE', 'AUTO', 'Complete vehicle protection insurance', 'Takaful', NULL, 1500.00, NULL, NULL, '{"vehicle_age_max": 10}', '["Comprehensive coverage", "24/7 roadside assistance", "Agency repair", "Fast claims settlement", "No claim bonus"]'),
('INS_HOME_01', 'HomeShield Property Takaful', 'INSURANCE', 'HOME', 'Home and contents protection', 'Takaful', NULL, 800.00, NULL, NULL, '{"property_type": ["house", "apartment"]}', '["Building and contents coverage", "Natural disaster protection", "Theft and burglary coverage", "Temporary accommodation", "Domestic helper coverage"]'),
('INS_HEALTH_01', 'HealthPlus Medical Takaful', 'INSURANCE', 'HEALTH', 'Comprehensive health insurance for families', 'Takaful', NULL, 3000.00, NULL, NULL, '{"age_max": 65}', '["Hospitalization coverage", "Outpatient benefits", "Dental and optical", "Maternity benefits", "Annual health screening"]'),
('INS_LIFE_01', 'FamilyFirst Life Takaful', 'INSURANCE', 'LIFE', 'Life protection with savings', 'Takaful', NULL, 2000.00, 40000, NULL, '{"age_min": 18, "age_max": 60}', '["Life protection", "Savings component", "Critical illness coverage", "Disability benefits", "Education fund option"]'),
('INS_BUSINESS_01', 'BizProtect Business Takaful', 'INSURANCE', 'BUSINESS', 'Comprehensive business insurance', 'Takaful', NULL, 5000.00, NULL, NULL, '{"business_type": ["SME", "Corporate"]}', '["Property and equipment coverage", "Business interruption", "Liability protection", "Employee compensation", "Cyber risk coverage"]'),

-- Investment Products (6 products)
('INV_EQUITY_01', 'Growth Equity Fund', 'INVESTMENT', 'EQUITY', 'Sharia-compliant equity investment fund', 'Musharakah', 8.5, 100.00, 50000, NULL, '{"risk_profile": "moderate to high"}', '["Diversified equity portfolio", "Professional fund management", "Quarterly dividend option", "Online portfolio tracking", "Regular market updates"]'),
('INV_SUKUK_01', 'StableSukuk Fund', 'INVESTMENT', 'FIXED_INCOME', 'Low-risk sukuk investment fund', 'Mudarabah', 4.5, 50.00, 25000, NULL, '{"risk_profile": "low"}', '["Government and corporate sukuk", "Stable returns", "Semi-annual profit distribution", "Capital preservation focus", "Liquidity facility available"]'),
('INV_BALANCED_01', 'BalancedGrowth Fund', 'INVESTMENT', 'BALANCED', 'Mixed asset investment fund', 'Mudarabah', 6.5, 75.00, 35000, NULL, '{"risk_profile": "moderate"}', '["60:40 equity-sukuk allocation", "Monthly rebalancing", "Moderate risk-return profile", "Flexible redemption", "Systematic investment plan"]'),
('INV_PROPERTY_01', 'RealtyIncome REIT Fund', 'INVESTMENT', 'PROPERTY', 'Real estate investment trust fund', 'Musharakah', 7.0, 200.00, 100000, NULL, '{"risk_profile": "moderate"}', '["Commercial property portfolio", "Rental income distribution", "Capital appreciation potential", "Quarterly valuations", "Geographic diversification"]'),
('INV_GOLD_01', 'GoldSecure Commodity Fund', 'INVESTMENT', 'COMMODITY', 'Gold-backed investment fund', 'Mudarabah', 5.0, 50.00, 20000, NULL, '{"risk_profile": "low to moderate"}', '["Physical gold backing", "Hedge against inflation", "No storage hassles", "Daily NAV updates", "Sharia-compliant structure"]'),
('INV_TECH_01', 'TechPioneers Fund', 'INVESTMENT', 'EQUITY', 'Technology sector focused fund', 'Musharakah', 12.0, 150.00, 75000, NULL, '{"risk_profile": "high"}', '["Global tech companies", "High growth potential", "Innovation focused", "Quarterly reporting", "Sector expertise"]');
