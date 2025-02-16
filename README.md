# FillWordFileBot 
## Telegram-бот для заполнения Word-документа по шаблону

## Начало работы
Бот начинает работу с команды `/start`

При запуске бота пользователю предлагается принять согласие на обработку персональных данных

Далее будет предложено заполнить следующую форму:
* Фамилия
* Имя
* Отчество
* Дата рождения
* Пол
* Фото

После ввода данных бот пришлет файл `review.docx` с заполненной формой.

## Результат работы
![result.png](images/result.png)
## Стек технологий:
* Backend -> Java (Spring Boot, Apache POI, Telegram API)
* DB -> PostgreSQL
* Container -> Docker
## Инструкция по запуску приложения
1. Клонируйте данный репозиторий в свою IDE
2. Запустите Docker Desktop на своём компьютере
3. Запустите терминал в корне приложения
4. В терминале введите следующую команду: `docker-compose up -d --build`
5. Запустите [FillWordFileBotApplication.java](src/main/java/ru/snptech/fillWordFileBot/FillWordFileBotApplication.java).

Бот доступен по следующему адресу: [@FillWordFileBot](https://t.me/fillWordFileBot)
