# PokeCompanion
- This app is a companion app for any main series Pokemon video game.
## Features

- Allows users to track their Pokemon's growth throughout their playthough so they can see the strengths and weaknesses it develops
- Allows users to predict how they will continue to grow
- Assists the user throughout their playthough by offering bits of advice concerning how to raise their Pokemon
- Provides an easy way to theorycraft and plan their perfect team for their game

## External requirements

- The app is being developed for android. There is not currently a testable build of the app yet.

## Setup

- There is not currently a testable build of the app available.

## Running

- There is not currently a testable build of the app available.

## Deployment

## Testing

The current working test for the method weightedAverage is located in /app/src/androidTest/Java/com/example/template and is called ExampleInstrumentedTest.
It can be ran by right clicking on the method and clicking run. The build will compile and the terminal will say "Test Ran Successfully" after it has completed.

Unfortunately, we built our behavioral test in Firebase, and from working on it learned that we must pay to actually test the code. We intend to potentially change our method of behavioral testing.

## Testing technology

## Running tests

## Authors

Nicholas Mueller  
Tyler Morehead  
Jacob Letizia  
JD Edwards  
Noah Jackson  


## Currently under construction [WIP]

- Login Screen [Tyler Morehead]
    
    Currently can only be accessed by a button from the home screen.
    User can input username and password, but there is no logging in yet

- Individual viewer [Nick Mueller]
    
    Currently can access page via button.
    Only stat calculation is implemented, but it is fully functioning

- Team Starter [Noah Jackson]

    You can generate random Pokemon Team from Pokemon currently stored on the app
    Only 6 member team or lower can be generated

- PokedexView [Jacob Letizia]

    Should be accessed via main page by button on the bottom
    Shows a view of all of the pokemon in the database (does not just show caught pokemon)
    WIP: Eventually will go to individual view for each pokemon when that is clicked from the list
    WIP: Add a search bar with options to restrict the search (ex: by type or by stats etc etc)

- Newsfeed [JD Edwards]

    Accessed from any page using the "home" button. Loads an RSS reader than displays articles from the pokemondb.net RSS feed.
    Features an instance of Chrome within the app so users can read an article without opening the link in a separate browser application.

- Settings [Jacob Letizia & JD Edwards]

    Displays two buttons for viewing a Frequently Asked Questions (FAQ) page and for logging out of the application.
