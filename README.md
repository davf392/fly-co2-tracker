# Fly Co2 Tracker

## Description

This app allows you to calculate in a very simple way the carbon footprint of a flight.

It's using the public algorithm created by futur eco: https://github.com/laem/futureco-data/blob/master/data/avion.yaml

The list of airports and their relative information are available [here](https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat)


## Method of calculation

### Inputs

--> Ville de départ ? (aeroportDepart ?)
--> Ville d'arrivée ? (aeroportArrivee ?)
--> allerRetour ? (true or false)
--> classeVoyage ? (1ère classe, 2nde classe)

### Algorithm

distanceVolAller = distanceBetween(aeroportDepart, aeroportArrivee)

empreinteVol = ( distanceVolAller * ( allerRetour ? 2 : 1 ) * ( impactCarboneUnitaire * coefficientForçageRadiatif * malusConfort )

Pourcentage du budget annuel de C02eq = ( empreinteVol / 2000 ) * 100


### impact carbone unitaire

if distanceVolAller < 500:
impactCarboneUnitaire = (0.161 + 0.181) / 2
else if distanceVolAller >= 500 && distanceVolAller < 1000:
impactCarboneUnitaire = 0.134
else if distanceVolAller >= 1000 && distanceVolAller < 2000:
impactCarboneUnitaire = 0.106
else if distanceVolAller >= 2000 && distanceVolAller < 5000:
impactCarboneUnitaire = 0.098
else
impactCarboneUnitaire = 0.083


### coefficient forçage radiatif 

estimation conservatrice : 2 (default)

estimation la + récente : 3


### malus confort

if classeVoyage = 'normal' (2nde classe)
(distanceVolAller < 1500km) ? 0.96 : 0.8
else if classeVoyage = 'business' (1ere classe)
malus = (distanceVolAller < 1500km) ? 1.26 : 1.54
else if classeVoyage = 'confort' (super confort VIP)
malus = 2.4


## Roadmap

- upload Flight carbon footprint app to new github repo
- find a good name for the flight carbon footprint calculator app
- send arguments (distance, oneway?, comfort) to ResultFragment
- auto-complete airport names using airlines.txt (optional: use IATA as filter)
- Implement carbon footprint algorithm & display on the 2nd fragment
- Display the percentage off the carbon annual budget
- Design of an horizontal bar chart to let the user compare the flight carbon footprint with :
  - Annual carbon budget required to respect Paris accords
  - Annual emissions per capita for France