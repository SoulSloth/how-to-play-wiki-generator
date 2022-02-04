# Why has God Abandoned Us?

> Seriously? Am I missing something?
>- Me RN

  JK, God loves all his children.
  
```clojure
  (defn blog-page
  "Displays blog entries"
  []
  (let
   [!blog-page (atom "")]
    (fetch-blog! !blog-page (get-blog-id-from-route))
    (reagent/create-class
     {:render (fn []
                [:div.blog
                 ;; >dangerous
                 [:div {:dangerouslySetInnerHTML {:__html (js/DOMPurify.sanitize @!blog-page)}}]])
      :component-did-update render-code})))
```
