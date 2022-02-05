(defproject how-to-play-wiki "0.1.0-SNAPSHOT"
  :description "Simple wiki static site generator for gaming websites"
  :license {:name "Attribution 3.0 Unported (CC BY 3.0)"
            :url "https://creativecommons.org/licenses/by/3.0/legalcode"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [stasis/stasis "2.5.1"]
                 [ring "1.9.5"]
                 [hiccup "1.0.5"]
                 [markdown-clj "1.10.8"]
                 [optimus "0.20.2"]
                 [enlive "1.1.6"]
                 [clygments "2.0.2"]]
  :ring {:handler how-to-play-wiki.core/app}
  :aliases {"build-site" ["run" "-m" "how-to-play-wiki.core/export"]}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]]}
             :uberjar {:aot :all}}
  :repl-options {:init-ns how-to-play-wiki.core}
  :main ^:skip-aot how-to-play-wiki.core
  :target-path "target/%s")
