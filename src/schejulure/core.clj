(ns schejulure.core
  (:require [clj-time.core :refer [date-time year minute hour day month day-of-week
                                   minutes in-seconds interval plus now]
             :as clj-time]
            [clj-time.local :refer [local-now]])
  (:import [java.util.concurrent Executors TimeUnit]))

(def pool (Executors/newScheduledThreadPool 1))

(defn current-minute
  "Given a DateTime, truncate to the current minute (removing seconds
   and millisecond components)"
  [time]
  (apply date-time ((juxt year month day hour minute) time)))

(defn next-minute
  "Given a DateTime, gives the next upcoming minute boundary"
  [time]
  (plus (current-minute time) (-> 1 minutes)))

(defn secs-to-next-minute
  "Given a time, will give the number of additional seconds required to
   move into the next minute"
  [time]
  (inc (in-seconds (interval time (next-minute time)))))

(defn call-every-second
  "Schedules a function to be called every second"
  [f]
  (.scheduleAtFixedRate pool f 1 1 TimeUnit/SECONDS))

(defn cron-of
  "Returns a cron-like vector. Note that the days range from 1-7 instead
   of traditional cron's 0-6.

   Do not use this! Use the far more convenient map format instead."
  [time]
  [(clj-time/second time)
   (minute time)
   (hour time)
   (day time)
   (month time)
   (day-of-week time)])

(defn has? [coll item] (some #{item} coll))
(defn all? [coll] (every? identity coll))

(defn cron-match?
  "Gives whether every element of a vector (a cron) can be found in the
   corresponding element of a vector of vectors (a cron range).

   e.g.
   (cron-match? [0 1] [[0 1 2] [0 1 2]]) => true
   (cron-match? [0 5] [[0 1 2] [0 1 2]]) => false"
  [cron cron-range]
  (all? (map has? cron-range cron)))

(def cron-defaults {:second 0
                    :minute (range 0 60)
                    :hour   (range 0 24)
                    :date   (range 1 32)
                    :month  (range 1 13)
                    :day    (range 1  8)})

(def day->number {:mon 1, 1 1
                  :tue 2, 2 2
                  :wed 3, 3 3
                  :thu 4, 4 4
                  :fri 5, 5 5
                  :sat 6, 6 6
                  :sun 7, 7 7, 0 7})

(def weekdays [:mon :tue :wed :thu :fri])
(def weekends [:sat :sun])

(defn keyword-day->number
  "Translates keywords e.g. :mon into the appropriate clj-time integer
   representation"
  [x]
  (if (coll? x) (map day->number x)
      (list (day->number x))))

(defn cronmap->cronrange [cronmap]
  (map (fn [x] (if (coll? x) x (list x)))
       (-> (merge cron-defaults cronmap)
           (update-in [:day] keyword-day->number)
           ((juxt :second :minute :hour :date :month :day)))))

(defn fire-scheduled
  "Given a map of firing times to functions, checks whether the current
   local time matches any of them and calls the ones that do, presumably
   for side effects"
  [scheduled-fns]
  (let [now (cron-of (local-now))]
    (doseq [[schedule f] scheduled-fns]
      (when (cron-match? now (cronmap->cronrange schedule))
        (try (f)
             (catch Throwable e
               (println "Caught exception in scheduled action " f " at " now)
               (.printStackTrace e)))))))

(defn schedule
  "Takes pairs of cron-maps with the function to call when that cron-map
   matches the current time.

   The default cron-map executes every minute of every day, add elements
   into the map to restrict this, for example {:day [:mon :tue] :hour 5}
   will execute every minute between 5 and 6am on mondays and tuesdays
   where {:minute [15 45]} will execute and quarter past and quarter to
   the hour, every hour every day.

   If an exception is thrown and uncaught by your, execution will
   continue rather than interrupt future scheduled executions, if you
   need to handle the error your function must catch it.

   All the scheduled tasks run in a single thread, therefore long
   running tasks may impact the execution of subsequent tasks. If your
   task takes a non trivial amount of time, have the scheduler fire off
   a future rather than running it directly."
  [& args]
  (let [scheduled-fns (partition 2 args)]
    (call-every-second #(fire-scheduled scheduled-fns))))
