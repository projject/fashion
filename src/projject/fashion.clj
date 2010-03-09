
(ns #^{:author "projject.com / Justin Streufert"
       :doc "'fashion' is a CSS domain-specific language for Clojure"}
  projject.fashion
  (:use projject.fashion.build
	[projject.fashion.unit-values :exclude [uv]]))

; Basic CSS rendering

(defn css
  "Render a stylesheet"
  [& nodes]
  (apply str (map #(render-node [] (transform-node %)) nodes)))

; Unit Values

(intern *ns* 'uv projject.fashion.unit-values/uv)

(defn uv-apply
  [unit fn values]
  (uv unit (apply fn values)))

(defmacro defuv [name value unit]
  `(def ~name (uv ~unit ~value)))

(defunit px)
(defunit em)
(defunit in)
(defunit cm)
(defunit mm)
(defunit ex)
(defunit pt)
(defunit pc)

; CSS Helpers

(load "fashion/helpers")
