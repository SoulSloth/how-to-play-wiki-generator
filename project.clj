(defproject how-to-play-wiki "0.1.0-SNAPSHOT"
  :description "Simple wiki static site generator for gaming websites"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [stasis/stasis "2.5.1"]
                 [ring "1.9.5"]
                 [hiccup "1.0.5"]
                 [markdown-clj "1.10.8"]
                 [enlive "1.1.6"]
                 [optimus "0.20.2"]
                 [markdown-clj "1.10.8"]]
  :ring {:handler how-to-play-wiki.core/app}
  :aliases {"build-site" ["run" "-m" "how-to-play-wiki.core/export"]}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]]}
             :uberjar {:aot :all}}
  :repl-options {:init-ns how-to-play-wiki.core}
  :main ^:skip-aot how-to-play-wiki.core
  :target-path "target/%s")
