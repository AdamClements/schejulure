(defproject schejulure "1.0.2-SNAPSHOT"
  :description "A simple long-term scheduling library inspired by cron and future"
  :url "http://www.github.com/AdamClements/schejulure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-time "0.6.0"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.1"]]}})
