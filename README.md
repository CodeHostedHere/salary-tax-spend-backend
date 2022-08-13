# salary-tax-spend


## General Description

A short project to learn Spring boot and Junit. Gets developer salary at 3 experience levels, tax, expenses costs and creates a short budget to compare the savings you would have as a developer in different countries.



### Operation

Some Key Points
* Using specified time limits and "updated_on" fields in the DB, information in this project expires and refetches information from its source. This also applies to currency conversion.
* Google Custom Search is used to scrape Glassdoor for Salary info as their URLs are not consistent.
* The Expense List, called 'Spend', is fetched straight from the website.
* Selenium is used to fetch Tax records for each country. It was picked to gain experience with the framework, some kind of library that can explore JS without a UI would be used in a real production environment
* Running each data fetching service with 'refreshAll' on startup to fill the DB isn't the most efficient method, it does produce cleaner code than checking only for newly inserted values in a loop at the start.
* A scheduler would need to be implemented to make this consistently check for new data on a long lived instance.
* A Secrets Manager is not used as this is purely run locally, the DB is used as stand-in.

### Setup

This setup requires a MySQL DB, API Keys for Google Custom search and a Currency Exchange API so I reckon it's too arduous to setup and play around with for any given reviewer.
