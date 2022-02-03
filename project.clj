(defproject how-to-play-wiki "0.1.0-SNAPSHOT"
  :description "Simple wiki static site generator for gaming websites"
  :url ""
  :license {:name "Attribution 3.0 Unported (CC BY 3.0)"
            :url "https://creativecommons.org/licenses/by/3.0/legalcode"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [stasis/stasis "2.5.1"]
                 [ring "1.9.5"]]
  :ring {:handler how-to-play-wiki.core/app}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]]}}
  :repl-options {:init-ns how-to-play-wiki.core})
