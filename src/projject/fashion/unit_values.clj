(ns projject.fashion.unit-values)

(defn uv-value
  [uv-or-val]
  (if (fn? uv-or-val)
    (uv-or-val :value)
    uv-or-val))

(defn uv-unit
  [unitval]
  (unitval :unit))

(defn uv
  [unit value]
  (fn 
    ([] (str value (name unit)))
    ([single]
       (cond (= single :unit) unit
	     (= single :value) value
	     true (uv unit (single value))))
    ([fn & args] (uv unit (apply fn (cons value (map uv-value args)))))))

(defmacro defunit [unit]
  `(defn ~unit [~'value] (uv (keyword '~unit) ~'value)))

