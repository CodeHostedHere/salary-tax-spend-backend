# salary-tax-spend


## General Description

A short project to familiarise myself with Spring boot, Junit, Mockito, and Selenium. Gets developer salary at 3 experience levels, tax, expenses costs and creates a short budget to compare the savings you would have as a developer in different countries.

There are inefficiencies which I have not optimised, instead leaving a comment to highlight them. The focus of the project is to gain familiarity with a few libraries so I'm fine to leave it not being as efficient as possible.

I have hit some database issues and will be retiring this project.

### Operation

* Using specified time limits and "updated_on" fields in the DB, information in this project expires and refetches information from its source. This also applies to currency conversion.
* Google Custom Search is used to scrape Glassdoor for Salary info as their URLs are not consistent.
* The Expense List, called 'Spend', is fetched straight from the website.
* Selenium is used to fetch Tax records for each country. It was picked to gain experience with the framework, some kind of library that can explore JS without a UI would be used in a real production environment
* A scheduler would need to be implemented to make this consistently check for new data on a long lived instance.
* A Secrets Manager is not used as this is purely run locally, the DB is used as stand-in.
* Tests are incomplete as it was just used to gain experience, rather than full test coverage

### Setup

This setup requires a MySQL DB, API Keys for Google Custom search and a Currency Exchange API so I reckon it's too arduous to setup and play around with for any given reviewer.
