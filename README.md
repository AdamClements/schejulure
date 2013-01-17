# Schejulure

A simple cron-inspired library for clojure

## Usage

    (def my-running-scheduler
      (schedule {:hour 12 :minute [0 15 30 45]} my-function
                {:hour (range 0 24 6) :minute 0 :day [:sat :sun]} batch-job))

This will start running straight away. This actually returns a future, which can be manipulated in all the same ways as a normal clojure future. Unlike a lot of libraries there isn't one central stateful scheduler in an atom, you can run as many as you like.

    (future-cancel my-running-scheduler)

The schedule map is modelled after crontabs, you can specify
{:minute :hour :date :month :day}
Each of these takes either a single value, or a list of values which when matched should fire the function. This means that you can use clojure's range and other list functions to generate for example every 5 minutes (range 0 60 5)

## License

Copyright © 2012 Adam Clements

Distributed under the Eclipse Public License, the same as Clojure.
