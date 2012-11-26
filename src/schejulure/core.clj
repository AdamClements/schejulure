(ns schejulure.core
  (:require [clj-time.core :refer [minute hour day month day-of-week]]
            [clj-time.local :refer [local-now]])
  (:import [java.util.concurrent Executors TimeUnit]))

(defn every-minute [f]
  (doto (Executors/newScheduledThreadPool 1)
    (.scheduleAtFixedRate f 0 60000 TimeUnit/MILLISECONDS)))

(defn cron-of [time]
  [(minute time)
   (hour time)
   (day time)
   (month time)
   (day-of-week time)])

(defn cron-match? [cron cron-range]
  (every? identity
          (map (fn [x coll] (some #(= x %) coll))
               cron cron-range)))

(def cron-defaults {:minute (range 0 60)
                    :hour   (range 0 24)
                    :date   (range 1 32)
                    :month  (range 1 13)
                    :day    (range 0  7)})

(def day->number {:sun 0 0 0
                  :mon 1 1 1
                  :tue 2 2 2
                  :wed 3 3 3
                  :thu 4 4 4
                  :fri 5 5 5
                  :sat 6 6 6})

(defn keyword-day->number [x]
  (if (coll? x) (map day->number x)
      (list (day->number x))))

(defn cronmap->cronrange [cronmap]
  (map (fn [x] (if (coll? x) x (list x)))
       (-> (merge cron-defaults cronmap)
           (update-in [:day] keyword-day->number)
           ((juxt :minute :hour :date :month :day)))))

(defn fire-scheduled [scheduled-fns]
  (let [now (cron-of (local-now))]
    (doseq [[schedule f] scheduled-fns]
      (when (cron-match? now (cronmap->cronrange schedule))
        (f)))))

(defn schedule [& args]
  (let [scheduled-fns (partition 2 args)]
    (every-minute (partial fire-scheduled scheduled-fns))))
