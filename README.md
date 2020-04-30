# Cuzoo

## What is cuzoo?
Cuzoo is a CRM software that visualizes sales processes. It provides a dashboard that gives an
overview of the sales process states (lead, prospect and quote), TODOs and contact points.
Additionally it provides a list view for companies, contacts and opportunities.  

### Glossary

<table>
    <tbody>
        <tr>
            <td>User</td>
            <td>User of the software (as a representative of his/her/its company).</td>
        </tr>
        <tr>
            <td>Contact</td>
            <td>
                Contact person for a company, contains information like phone number, email address  
                and the role in the company.
            </td>
        </tr>
        <tr>
            <td>Contact Point</td>
            <td>
                A contact point is an event where the user was in any kind of (relevant)
                interaction with a contact. Contains information like the date of the event, what happened,
                the contact and a related opportunity.
            </td>
        </tr>
        <tr>
            <td>Opportunity</td>
            <td>
                An opportunity describes the possibility of a business with a company.
                It includes a history of contact points, a status and the progress. An opportunity starts with
                the status lead and can then evolve into a prospect or a quote due to new contact points.
            </td>
        </tr>
        <tr>
            <td>Lead</td>
            <td>A potential customer. No information about means, requirements or conditions.</td>
        </tr>
        <tr>
            <td>Prospect</td>
            <td>The user has more information, the potential customer fits some criteria.</td>
        </tr>
        <tr>
            <td>Quote</td>
            <td>
                Means, requirements and conditions are clarified. The user as well as the potential
                customer is interested.
            </td>
        </tr>
        <tr>
            <td>Win</td>
            <td>
                An opportunity results in coming down to business with a company.
            </td>
        </tr>
        <tr>
            <td>Lose</td>
            <td>An opportunity ends in not coming down to business with a company.</td>
        </tr>
    </tbody>
</table>

## Project components
Backend: Java, Spring Boot  
Frontend: Vue.js, Vuetify  
Database: Postgres  
Runs on Docker


## import project to IDE
backend and frontend directory have to be imported seperately


# cuzoo-backend

## get Database
change to "/cuzoo" directory
```
docker-compose up postgre-db
```
(might need to install docker compose)

## run application
to run the application with demo profile:  
set  
IntelliJ > Run/Debug Configurations > SpringBoot/Application > Configuration >
Active profiles:  
to  
demo

### Log In
You can log in with user: 'demo' and password: 'password', or create a new user
with a username and a password of your choice. The password has to be hashed
with bcrypt.
Online hashgenerator: https://www.browserling.com/tools/bcrypt


# cuzoo-frontend

## Project setup
```
yarn install
```

### Compiles and hot-reloads for development
```
yarn run serve
```

### Compiles and minifies for production
```
yarn run build
```

### Run your tests
```
yarn run test
```

### Lints and fixes files
```
yarn run lint
```

### Run your unit tests
```
yarn run test:unit
```
