(ns schejulure.core-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :refer [date-time]]
            [schejulure.core :refer :all]))

(fact "I have tests"
      (identity true) => true)

(facts "cron-of basically works"
      (cron-of (date-time 1986 10 23 16 30)) => [30 16 23 10 4]
      (cron-of (date-time 1986 10 22 8 30)) => [30 8 22 10 3]
      (cron-of (date-time 2000 1 1 0 0)) => [0 0 1 1 6]
      (cron-of (date-time 2000 1 2 0 0)) => [0 0 2 1 0])

(facts "cron-match can match crons"
       (cron-match? [0 0 2 3 4] [[0 1 2] [0 1 2] [0 1 2] [0 1 2] [0 1 2]]) => false
       (cron-match? [0 0 2 1 1] [[0 1 2] [0 1 2] [0 1 2] [0 1 2] [0 1 2]]) => true
       (cron-match? [0 0 2 3 4] (cronmap->cronrange cron-defaults)) => true)
