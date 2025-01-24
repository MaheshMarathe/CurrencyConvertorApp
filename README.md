# CurrencyConvertorApp

Currency Converter App that’s perfect for anyone diving into Android development and looking to work with modern components and best practices.

1. MVVM with clean architecture
2. Hilt
3. Jetpack Compose 
4. Room 
5. Retrofit 
6. Junit 
7. Mockk 
8. Coroutines
9. Flows

● The required data is fetched from the open exchange rates service.
○ See the documentation for information on how to use their API.
○ You must use a free account - not a paid one.
○ Get a free App ID that will give you access to the Open Exchange Rates API
here.

● The required data is persisted locally to permit the application to be used
offline after data has been fetched.
● In order to limit bandwidth usage, the required data is refreshed from the API no
more frequently than once every 30 minutes.
● The user is able to select a currency from a list of currencies provided by open
exchange rates.
● The user is able to enter the desired amount for the selected currency.
● The user is then shown a list showing the desired amount in the selected
currency converted into amounts in each currency provided by open exchange rates.
○ If exchange rates for the selected currency are not available via open
exchange rates, App performs the conversions on the app side.
○ When converting, floating point errors are acceptable.

![currency convertor](https://github.com/user-attachments/assets/e4ea7a3b-4768-4ed5-aefc-10ced3f09745)
