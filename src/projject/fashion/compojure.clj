(ns projject.fashion.compojure)

(defn serve-css
  "Serves text as CSS properly for Compojure"
  [css]
  {:status 200
   :headers {"Content-Type" "text/css"}
   :body css})