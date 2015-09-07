; Delivery Bot

(define (domain DELIVERY)
  (:requirements :strips)

  (:predicates (in ?item ?room)
               (empty)
               (connected ?room1 ?room2)
               (can-move ?being)
               (holding ?item))
  (:action pick-up
              :parameters (?item ?room ?being)
              :precondition (and (empty) (in ?item ?room) (in ?being ?room) (can-move ?being))
              :effect
              (and (holding ?item)
                   (not (empty))
                   (not (in ?item ?room))))
  (:action move
              :parameters (?room1 ?room2 ?being)
              :precondition (and (in ?being ?room1) (connected ?room1 ?room2) (can-move ?being)) ;;;(or (connected ?room1 ?room2) (connected ?room2 ?room1)) doesn't work.
              :effect
              (and (not (in ?being ?room1))
                   (in ?being ?room2)))
  (:action put-down
              :parameters (?item ?room ?being)
              :precondition (and (in ?being ?room) (holding ?item) (not (empty)) (can-move ?being))
              :effect
              (and (empty)
                   (in ?item ?room)
                   (not (holding ?item))
              ))
)