# Theatrical Players Refactoring Kata

[![test](https://github.com/LarsEckart/Enterprise-Theatrical-Players-Refactoring-Kata/actions/workflows/test.yml/badge.svg)](https://github.com/LarsEckart/Enterprise-Theatrical-Players-Refactoring-Kata/actions/workflows/test.yml)

Imagine a company of theatrical players who go out to various events performing plays.
Typically, a customer will request a few plays and the company charges them based on the size of the audience and the
kind of play they perform.
There are currently two kinds of plays that the company performs: tragedies and comedies.
As well as providing a bill for the performance, the company gives its customers "volume credits" which they can use for
discounts on future performances — think of it as a customer loyalty mechanism.

## Changes

* We want a new endpoint to return the statement data as json.

```json
{
  "headline": "Statement for BigCo",
  "plays": [
    {
      "play": "As You Like It",
      "seats": 10,
      "cost": "$330.00"
    },
    {
      "play": "Othello",
      "seats": 20,
      "cost": "$400.00"
    }
  ],
  "total": "$1,075.00",
  "credits": 5
}
```

* The players are looking to perform more kinds of plays: they hope to add history, pastoral, pastoral-comical,
  historical- pastoral, tragical-historical, tragical-comical-historical-pastoral, scene individable, and poem unlimited
  to their repertoire.
