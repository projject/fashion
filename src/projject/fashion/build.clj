(ns projject.fashion.build
  (:require [clojure.contrib.str-utils2 :as str2]))

(defn transform-selector
  "Add a space to the beginning of the selector unless it begins with a %"
  [selector]
  (if (= \% (first selector))
    (str2/drop selector 1)
    (str " " selector)))

(defn selector-path-list
  "[[1 2] [x y] [A]] => [[1 x A] [1 y A] [2 x A] [2 y A]]"
  [parents [element & rest-of-elements]]
  (let [paths (map #(conj parents (transform-selector %)) element)]
    (if (empty? rest-of-elements)
      paths
      (apply concat (map #(selector-path-list % rest-of-elements) paths)))))

(defn ensure-vector
  "Returns vectors unchanged. Other values are returned in a vector."
  [candidate]
  (if (vector? candidate)
    candidate
    [candidate]))

(defn render-selector-chain
  "Render a CSS selector chain (i.e. A .thing:hover, BUTTON .thing:hover)"
  [chain]
  (let [chain (map ensure-vector chain)
	paths (selector-path-list [] chain)
	paths (map #(str2/ltrim (apply str %)) paths)]
    (str2/join ", " paths)))

(defn transform-node
  "Organize the contents of a node into (selector, rules, children)"
  [[selector & contents]]
  (list
   (if (keyword? selector) (name selector) selector)
   (apply merge (filter map? contents))
   (filter vector? contents)))

(defn render-value
  "Render CSS value text (i.e. 15px, bold, 1px solid red)"
  [value]
  (cond (fn? value) (value)
	(keyword? value) (name value)
	(sequential? value) (str2/join " " (map render-value value))
	true value))

(defn render-rule
  "Render a CSS rule (i.e. font-weight: normal)"
  [[key value]]
  (str "  " (name key) ": " (render-value value) ";\n"))

(defn render-node
  "Render a vector containing a selector, rules and children to CSS"
  [upstream-selector-chain [selector rules children]]
  (let [selector-chain (conj upstream-selector-chain selector)
	selector-text  (render-selector-chain selector-chain)
	rule-text      (apply str (map render-rule rules))
	children       (map transform-node children)
	sub-text       (apply str (map #(render-node selector-chain %) children))]
    (if (empty? rule-text)
      sub-text
      (str selector-text " {\n" rule-text "}\n\n" sub-text))))

