# Twente Canal Digital Twin Demonstrator

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.18722245.svg)](https://doi.org/10.5281/zenodo.18722245)

Corridor-scale Digital Twin for Inland Waterway Logistics



## 1. Project Overview

This repository contains the implementation of a corridor-scale Digital Twin demonstrator developed for the Twente Canal in the Netherlands.

The system integrates water level data, weather forecasts, vessel AIS information, and canal infrastructure data into a unified interactive dashboard. The objective is to improve situational awareness and support short-term operational planning in inland waterway logistics.

This demonstrator was developed as part of an Engineering Doctorate (EngD) project at the University of Twente.

The system is designed to support decision-making, not to replace human operators.

<img width="1861" height="1029" alt="DT03" src="https://github.com/user-attachments/assets/aef23c80-5ae4-447d-beb3-289a6adf5abe" />
<img width="1863" height="1027" alt="DT02" src="https://github.com/user-attachments/assets/e605ec75-6451-4ec1-8c82-af624615da3b" />
<img width="1865" height="1027" alt="DT01" src="https://github.com/user-attachments/assets/98af69d5-3fff-4450-bb93-54d065502103" />

## 2. Research Context

This implementation corresponds to the demonstrator described in the EngD thesis:

**Development of a Digital Twin for Resilient Multimodal Corridors**

The research followed three main stages:

1. Stakeholder requirement analysis
2. Identification and prioritisation of use cases
3. Development and validation of a working prototype

Based on stakeholder prioritisation, the following high-priority use cases were implemented:

* Water Level Prediction and Real-Time Weather Monitoring
* Vessel Monitoring (AIS-based tracking)
* Canal Infrastructure Monitoring

A logistics publish-and-match function was also developed as a proof-of-concept module.


## 3. System Architecture

The platform follows a front-end and back-end separated architecture.

Frontend (Vue + OpenLayers)
↓ REST API
Backend (Spring Boot)
↓
MySQL Database
External Data Sources (AIS, Rijkswaterstaat, Weather APIs)

The backend integrates external data sources and provides RESTful services.
The frontend visualises the information through an interactive dashboard.


## 4. Backend Structure (Spring Boot)

The backend is developed using Spring Boot and follows a layered architecture:

* controller/ → API endpoints
* service/ → business logic
* mapper/ → database operations
* entity/ → data models
* config/ → system configuration

Main responsibilities:

* Integration of water level and weather data
* AIS vessel data management
* Infrastructure data handling
* Logistics publish and matching logic
* Data storage and retrieval (MySQL)

The backend exposes REST APIs consumed by the frontend.


## 5. Frontend Structure (Vue + OpenLayers)

The frontend provides a map-based dashboard interface.

Main responsibilities:

* Interactive map visualisation
* Layer management (water level, vessel, infrastructure)
* Dashboard panels for monitoring data
* Click-based feature inspection
* Logistics publishing and matching interface

Typical structure:

src/
├── views/
├── components/
├── api/
├── router/
└── utils/

The frontend communicates with the backend via RESTful APIs.


## 6. Implemented Functional Modules

### 6.1 Water Level and Weather Monitoring

* Display of real-time water level observations
* Integration of short-term weather forecast
* Monitoring of key upstream stations
* Support for low-water situational awareness

### 6.2 Vessel Monitoring (AIS)

* Real-time vessel position display
* Vessel information retrieval on click
* Corridor traffic overview

### 6.3 Canal Infrastructure Monitoring

* Visualisation of locks and other infrastructure elements
* Attribute inspection via map interaction
* Basic operational awareness

### 6.4 Logistics Publish and Match (Prototype)

* Publication of cargo demand or vessel availability
* Matching function (cargo-to-vessel / vessel-to-cargo)
* Transparency support for logistics coordination

This module is demonstrative and not fully integrated into external planning systems.


## 7. Validation Context

The demonstrator was validated through:

* Semi-structured stakeholder interviews
* Scenario-based system walkthroughs
* Questionnaire-based evaluation (including SUS)

The system was generally perceived as a useful integrated overview tool that reduces the need to consult multiple separate platforms.

Identified improvement areas include:

* Integration of navigable depth (MGD) data
* Route-based monitoring logic
* Alert mechanisms
* Extended water level forecasting


## 8. How to Run the Project

### Backend

cd backend
mvn spring-boot:run

Make sure:

* MySQL is running
* Database configuration in application.yml is correct


### Frontend

cd frontend
npm install
npm run dev

The frontend connects to the backend via configured API endpoints.


## 9. Target Stakeholders

The system is designed for:

* Industrial shippers
* Logistics service providers
* Vessel operators
* Port authorities
* Infrastructure managers


## 10. Limitations

* Limited validation sample size
* Forecasting horizon is short-term
* Logistics matching is a proof-of-concept
* No API integration with external planning systems yet


## 11. Academic Context

Engineering Doctorate Programme
University of Twente
Faculty of Engineering Technology

This repository represents a validated Digital Twin demonstrator supporting corridor-scale inland waterway logistics research.








