@echo off
set /p changes="Enter Changes: "
git add .
git commit -m "%changes%"
git push heroku master
heroku logs -t