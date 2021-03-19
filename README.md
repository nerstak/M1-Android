# M1-Android

## Table of contents

- [Informations](#Informations)

- [Features](#Features)

- [Installation](#Installation)

## Informations

### Description

Android Application to track books and reading progress of an user, using Google Books API.

### Technologies/framework used

Built with

- Java

- Android Studio

Database

- SQLite

API : 

- Google Books API

## Features

This project enables any user to track their progress on books.
They can :

- see their bookshelf (with data conserved locally)
- search books (with reactive history of searches)
- add and remove books
- see details of their books (cover, title, publication date, number of pages, resume (click on details to see it))
- update the reading progress (unknown, reading, on hold, re-reading, dropped, plan to read, done)
- update the number of pages read
- rate the book

## Installation

First, generate your own API key [here](https://developers.google.com/books/docs/v1/using).

Copy the file `app\src\main\res\values\secrets_TEMPLATE.xml` to `app\src\main\res\values\secrets.xml` , and edit it to add you consumer key. Rename `CONSUMER_KEY_TEMPLATE` to `CONSUMER_KEY`.

