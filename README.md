# Calculateur d'empreinte carbone d'un vol en avion

## Roadmap

Design d'un horizontal bar chart pour comparer l'empreinte du vol avec:
- budget carbone annuel pour respecter les accords de Paris
- moyenne des émissions d'un français

## Information sur les aéroports

https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat

## Algorithme

Inputs :

--> Ville de départ ? (aeroportDepart ?)
--> Ville d'arrivée ? (aeroportArrivee ?)
--> allerRetour ? (true or false)
--> classeVoyage ? (1ère classe, 2nde classe)

## Formule de calcul de l'empreinte carbone

Le simulateur est open sources et les données sont publiques :
https://github.com/laem/futureco-data/blob/master/data/avion.yaml

distanceVolAller = distanceBetween(aeroportDepart, aeroportArrivee)

empreinteVol = ( distanceVolAller * ( allerRetour ? 2 : 1 ) * ( impactCarboneUnitaire * coefficientForçageRadiatif * malusConfort )

Pourcentage du budget annuel de C02eq = ( empreinteVol / 2000 ) * 100


###### impact carbone unitaire ######

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


###### coefficient forçage radiatif ######

estimation conservatrice : 2 (default)

estimation la + récente : 3


###### malus confort ######

if classeVoyage = 'normal' (2nde classe)
(distanceVolAller < 1500km) ? 0.96 : 0.8
else if classeVoyage = 'business' (1ere classe)
malus = (distanceVolAller < 1500km) ? 1.26 : 1.54
else if classeVoyage = 'confort' (super confort VIP)
malus = 2.4

