(ns schejulure.core-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :refer [date-time]]
            [schejulure.core :refer :all]))

(fact "I have tests"
      (identity true) => true)

(facts "cron-of basically works"
      (cron-of (date-time 1986 10 23 16 30)) => [0 30 16 23 10 4]
      (cron-of (date-time 1986 10 22 8 30)) => [0 30 8 22 10 3]
      (cron-of (date-time 1986 10 22 8 30 10)) => [10 30 8 22 10 3]
      (cron-of (date-time 2000 1 1 0 0)) => [0 0 0 1 1 6]
      (cron-of (date-time 2000 1 2 0 0)) => [0 0 0 2 1 7])

(facts "cronmap->cronrange works"
       (cronmap->cronrange {:minute 10 :hour 10 :date 10 :month 1 :day :mon})  => '((0) (10) (10) (10) (1) (1))
       (cronmap->cronrange {:second 0 :minute 10 :hour 15 :date [1 3 5] :month (range 1 10 1) :day :mon})  => '((0) (10) (15) (1 3 5) (1 2 3 4 5 6 7 8 9) (1))
       (cronmap->cronrange {:minute 10 :hour 10 :date 10 :month (range 1 4 1) :day :mon})  => '((0) (10) (10) (10) (1 2 3) (1))
       (cronmap->cronrange {:second 10 :minute 10 :hour 10 :date 10 :month [1 3 5] :day :mon})  => '((10) (10) (10) (10) (1 3 5) (1))
       (cronmap->cronrange {:second 59 :minute 10 :hour 10 :date 10 :day :mon})  => '((59) (10) (10) (10) (1 2 3 4 5 6 7 8 9 10 11 12) (1) )
       (cronmap->cronrange {:second 1 :minute 10 :hour 10 :date 10 :month 1 :day [:mon :tue :fri]})  => '((1) (10) (10) (10) (1) (1 2 5))

)

(facts "cron-match can match crons"
       (cron-match? [0 0 2 3 4] [[0 1 2] [0 1 2] [0 1 2] [0 1 2] [0 1 2]]) => false
       (cron-match? [0 0 2 1 1] [[0 1 2] [0 1 2] [0 1 2] [0 1 2] [0 1 2]]) => true
       (cron-match? [0 0 2 3 4] (cronmap->cronrange cron-defaults)) => true)
