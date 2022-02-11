(ns how-to-play-wiki.core
  (:require [stasis.core :as stasis]
            [hiccup.page :refer [html5]]
            [clojure.java.io :as io]
            [markdown.core :as md]
            [clojure.string :as str]
            [optimus.link :as link]
            [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]]
            [optimus.export]
            [how-to-play-wiki.pages :as pages])
  (:gen-class))

(defn get-assets
  "Load all assets in public"
  []
  (assets/load-assets "optimusAssets" [#".*\.(webp|png|css)$"]))

(defn layout-page
  "Template a body into a basic page"
  [request page]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:title "How to Play Wiki"]
    [:link {:rel "stylesheet" :href (link/file-path request "/styles/styles.css")}]]
   [:body.page
    [:a.siteTitle {:href "/"} [:h1 "Elden Ring Wiki"]]
    ;;TODO: Generate this post re-catagorization
    [:nav
     [:ul
      [:a {:href "/weapons"} [:li "Weapons"]]
      [:a {:href "/classes"} [:li "Classes"]]
      [:a {:href "/locations"} [:li "Locations"]]
      [:a {:href "/enemies"} [:li "Enemies"]]
      [:a {:href "/items"} [:li "Items"]]
      [:a {:href "/about"} [:li "About"]]]]
    [:div.body page]]))

(defn edn-layout
  "Layout an edn file"
  [{category :category :as page} pages req]
  (case category
    :classes (pages/class-page page req)
    :about (pages/about-page page)
    :enemies (pages/enemy-page page)
    :weapons (pages/weapon-page page)
    :locations (pages/location-page page)
    :items (pages/enemy-page page)
    :home (pages/home-page page)
    :directory (pages/directory-page page pages)))

(defn edn-pages
  "{:path :edn-file} -> {:path :f(request)-> html-file}"
  [pages]
  (zipmap (map
           #(-> %
                (str/replace #"\.edn$" "/")
                ;;TODO: dirty hack to make index.edn files the root pages for their root
                ;; i.e. /index/ route becomes / or /enemies/index/ becomes /enemies/
                (str/replace #"index/" ""))
           (keys pages))
          (map #(fn [req] (layout-page req (edn-layout (read-string %) pages req))) (vals pages))))

(defn get-raw-pages
  "Returns a map of {:routes f(raw-file)-> :prepared-page}"
  [resource-dir]
  (stasis/merge-page-sources
   {:edn
    (edn-pages (stasis/slurp-directory (str resource-dir "/edn") #".*\.edn$"))}))

(defn prepare-page
  "Do any prepossessing a page needs here"
  [page req]
  ;; -> this in the future
  (if (string? page) page (page req)))

(defn prepare-pages
  "{:route :page} -> {:route :f(req) -> page}"
  [pages]
  (zipmap (keys pages)
          (map #(partial prepare-page %) (vals pages))))

(defn get-pages
  "Return a map of paths->functions returning pages. Lazy pages"
  [resource-dir]
  (prepare-pages (get-raw-pages resource-dir)))

;;Ring handler for development server
;;TODO: Make this a development dep instead
(def app
  (optimus/wrap (stasis/serve-pages (get-pages "site-content"))
                get-assets
                optimizations/all
                serve-live-assets))

(defn export
  "Export the static site to some directory"
  [resource-dir export-dir]
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-raw-pages resource-dir) export-dir {:optimus-assets assets})))

;; (defn export [resource-dir export-dir]
;;   (stasis/export-pages (get-pages resource-dir) export-dir))

(defn -main
  "Function for our uberjar to run"
  [resource-dir export-dir & args]
  (export resource-dir export-dir))
