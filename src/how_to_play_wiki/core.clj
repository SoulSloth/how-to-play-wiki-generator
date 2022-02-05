(ns how-to-play-wiki.core
  (:require [stasis.core :as stasis]
            [optimus.link :as link]
            [hiccup.page :refer [html5]]
            [clojure.java.io :as io]
            [markdown.core :as md]
            [clojure.string :as str]
            [optimus.export]
            [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]])
  (:gen-class))

(defn get-assets
  "Import assets into optimus"
  []
  (assets/load-assets "optimusStayInYourLane/public" [#".*"]))

(defn layout-page
  "Template a body into a basic page"
  [request page]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:title "Tech blog"]
    [:link {:rel "stylesheet" :href (link/file-path request "/styles/styles.css")}]]
   [:body
    [:div.logo "howtoplaywiki.no"]
    [:div.body page]]))

(defn partial-pages
  "Transform a map of partials into a map full html pages"
  [pages]
  (zipmap (keys pages)
          (map #(fn [req] (layout-page req %)) (vals pages))))

(defn markdown-pages
  "Turn a map of markdown pages into a map of html pages"
  [pages]
  ;;Pages must end in a file extension or slash
  (zipmap (map #(str/replace % #"\.md$" "/") (keys pages))
          (map #(fn [req] (layout-page req (md/md-to-html-string %))) (vals pages))))

(defn get-raw-pages
  "Return a map of routes to page contents for resources, Throws a fit if we have path conflicts"
  [resource-dir]
  (stasis/merge-page-sources
   {:public
    (stasis/slurp-directory (str resource-dir "/public") #".*\.(html|css|js)$")
    :partials
    (partial-pages (stasis/slurp-directory (str resource-dir "/partials") #".*\.html$"))
    :markdown
    (markdown-pages (stasis/slurp-directory (str resource-dir "/markdown") #"\.md$"))}))

(defn prepare-page
  "Do any prepossessing a page needs here"
  [page req]
  ;; -> this in the future
  (if (string? page) page (page req)))

(defn prepare-pages
  "Turns our raw pages into functions"
  [pages]
  (zipmap (keys pages)
          (map #(partial prepare-page %) (vals pages))))

(defn get-pages
  "Return a map of paths->functions returning pages. Lazy pages"
  [resource-dir]
  (prepare-pages (get-raw-pages resource-dir)))

;; Server handler we pass to RING
(def app
  get-pages
   )

(defn export [resource-dir export-dir]
    (stasis/export-pages (get-pages resource-dir) export-dir))

;;TODO: jar deployment? May not be needed if we never deploy as a server
(defn -main
  [& args]
  (export (first args) (second args)))
