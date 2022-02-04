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
            [optimus.strategies :refer [serve-live-assets]]))

(defn get-assets
  "Import assets into optimus"
  []
  (assets/load-assets "public" [#".*"]))

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
  []
  (stasis/merge-page-sources
   {:public
    (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
    :partials
    (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))
    :markdown
    (markdown-pages (stasis/slurp-directory "resources/markdown" #"\.md$"))}))

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
  []
  (prepare-pages (get-raw-pages)))

;; Server handler we pass to RING
(def app
  (optimus/wrap
   (stasis/serve-pages get-pages)
   get-assets
   ;;Perform all optimizations
   optimizations/all
   ;;Optimus reads assets from disk on all requests
   ;;TODO: What happens when we export a static site?
   serve-live-assets))

(def export-dir "dist")

(defn export []
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
