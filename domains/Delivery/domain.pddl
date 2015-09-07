; Delivery Bot

(define (domain DELIVERY)
    (:requirements :strips)
    (:predicates (holding ?x ?y)
            (has-item ?x)
            (connected ?x ?y)
            (in ?x ?y)
            (can-move ?x))

    (:action pick-up
            :parameters (?x ?y ?z)
            :precondition (and (in ?x ?y) (in ?z ?y) (not (has-item ?z)) (can-move ?z))
            :effect (and (holding ?x ?z) (has-item ?z) (not (in ?x ?y)))
    )

    (:action put-down
            :parameters (?x ?y ?z)
            :precondition (and (in ?z ?y) (has-item ?z) (holding ?x ?z) (can-move ?z))
            :effect
            (and (in (?x ?y))
                (not (holding ?x ?z))
                (not (has-item ?z))
            )
    )

    (:action move-to
        :parameters (?x ?y ?z)
        :precondition (and (in ?z ?x) (connected ?x ?y) (can-move ?z))
        :effect
        (and
            (in ?y ?z)
            (not (in ?x ?z))
        )
    )
)