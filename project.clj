(defproject schejulure "0.1.2"
  :description "A simple long-term scheduling library inspired by cron and future"
  :url "http://www.github.com/AdamClements/schejulure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-time "0.4.4"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]]
                   :plugins [[lein-midje "2.0.1"]]}})
