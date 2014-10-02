# Schejulure

A simple cron-inspired library for clojure

## Usage

Include the library in your leiningen project dependencies:

```clojure
[schejulure "1.0.1"]
```

Then use it to schedule something:

```clojure
(use 'schejulure.core)

(def my-running-scheduler
  (schedule {:hour 12 :minute [0 15 30 45]} my-function
    {:hour (range 0 24 6) :minute 0 :day [:sat :sun]} batch-job))
```

This will start running straight away. This actually returns a future, which can be manipulated in all the same ways as a normal clojure future. Unlike a lot of libraries there isn't one central stateful scheduler in an atom, you can run as many as you like.

```clojure
(future-cancel my-running-scheduler)
```

The schedule map is modelled after crontabs, you can specify _{:minute :hour :date :month :day}_
Each of these takes either a single value, or a list of values which when matched should fire the function. This means that you can use clojure's range and other list functions to generate for example every 5 minutes (range 0 60 5)

Exceptions will be caught and a stacktrace printed rather than affect
the execution of subsequent scheduled tasks. If you wish to handle
exceptions, you must catch them within the function you pass to the
scheduler.

The scheduler runs in a single thread, so long running tasks have the
potential to block future tasks from executing. If you have any tasks
which take a non-trivial amount of time to run, it's recommended you
fire a future from within the scheduler, allowing it to continue with
other tasks.

## Changelog

### 1.0.1
* Change caught exceptions to Throwable so nothing can kill the scheduler inadvertently

### 0.1.4

* Change the initial delay to automatically start on the next minute boundary on the clock rather than a minute after invocation.
* Enhance docstrings
* Add in convenience functions for next-minute current-minute and secs-to-next-minute boundary

### 0.1.3

* Bugfix - Schejulure no longer observes the sabbath (i.e. it works on Sundays)
* Add in convenience weekends vector

## License

Copyright © 2012-2013 Adam Clements

Distributed under the Eclipse Public License, the same as Clojure.
