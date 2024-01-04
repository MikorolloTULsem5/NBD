//    @Override
//    public boolean create(ReservationMapper reservationMapper) {
//        try {
//            //Check client
//            var list1 = getDatabase().getCollection("clients", ClientMapper.class)
//                    .find(Filters.eq("_id", reservationMapper.getClientId())).into(new ArrayList<>());
//            if (list1.isEmpty()) {
//                throw new ReservationException("Brak podanego klienta w bazie!");
//            }
//            Client clientFound = ClientMapper.fromMongoClient(list1.get(0));
//
//            //Check court
//            var list2 = getDatabase().getCollection("courts", CourtMapper.class)
//                    .find(Filters.eq("_id", reservationMapper.getCourtId())).into(new ArrayList<>());
//            if (list2.isEmpty()) {
//                throw new ReservationException("Brak podanego boiska w bazie!");
//            }
//            Court courtFound = CourtMapper.fromMongoCourt(list2.get(0));
//
//            if (!courtFound.isRented() && !clientFound.isArchive() && !courtFound.isArchive()) {
//                InsertOneResult result;
//                ClientSession clientSession = getMongoClient().startSession();
//                try {
//                    clientSession.startTransaction();
//                    result = this.getCollection().insertOne(clientSession, ReservationMapper.toMongoReservation(
//                            new Reservation(UUID.fromString(reservationMapper.getId()),
//                                    clientFound, courtFound, reservationMapper.getBeginTime())));
//                    if (result.wasAcknowledged()) {
//                        getDatabase().getCollection("courts", CourtMapper.class).updateOne(
//                                clientSession,
//                                Filters.eq("_id", courtFound.getCourtId().toString()),
//                                Updates.inc("rented", 1));
//                    }
//                    clientSession.commitTransaction();
//                } catch (Exception e) {
//                    clientSession.abortTransaction();
//                    clientSession.close();
//                    throw new MyMongoException(e.getMessage());
//                } finally {
//                    clientSession.close();
//                }
//                return result.wasAcknowledged();
//            } else if (clientFound.isArchive()) {
//                throw new ClientException("Nie udalo sie utworzyc rezerwacji - klient jest archiwalny!");
//            } else if (courtFound.isArchive()) {
//                throw new CourtException("Nie udalo sie utworzyc rezerwacji - boisko jest archiwalne!");
//            } else {
//                throw new ReservationException("To boisko jest aktualnie wypozyczone!");
//            }
//        } catch (MongoWriteException | MongoCommandException exception) {
//            throw new MyMongoException(exception.getMessage());
//        }
//    }





//    public void update(Court court, LocalDateTime endTime) {
//        //Find court
//        var listCourt = getDatabase().getCollection("courts", CourtMapper.class)
//                .find(Filters.eq("_id", court.getCourtId().toString())).into(new ArrayList<>());
//        if (listCourt.isEmpty()) {
//            throw new ReservationException("Brak podanego boiska w bazie!");
//        }
//        if (listCourt.get(0).isRented() == 0) {
//            throw new ReservationException("To boisko nie jest aktualnie wypozyczone!");
//        }
//
//        //Find reservation
//        var listReservation = getDatabase().getCollection("reservations",
//                ReservationMapper.class).find(Filters.eq("courtid", court.getCourtId().toString()))
//                .into(new ArrayList<>());
//        if (listReservation.isEmpty()) {
//            throw new ReservationException("Brak rezerwacji, dla podanego boiska, w bazie!");
//        }
//
//        //Find client
//        var listClient = getDatabase().getCollection("clients", ClientMapper.class)
//                .find(Filters.eq("_id", listReservation.get(0).getClientId().toString()))
//                .into(new ArrayList<>());
//        if (listClient.isEmpty()) {
//            throw new ReservationException("Brak podanego klienta w bazie!");
//        }
//
//        Reservation reservationFound = ReservationMapper.fromMongoReservation(listReservation.get(0),
//                listClient.get(0), listCourt.get(0));
//
//        ClientSession clientSession = getMongoClient().startSession();
//        court.setRented(false);
//        try {
//            clientSession.startTransaction();
//            reservationFound.endReservation(endTime);
//
//            //Update reservations properties
//            update(reservationFound.getId(), "endtime", reservationFound.getEndTime());
//            update(reservationFound.getId(), "reservationcost", reservationFound.getReservationCost());
//
//            //Update court's "rented" field
//            getDatabase().getCollection("courts", CourtMapper.class).updateOne(
//                    clientSession,
//                    Filters.eq("_id", listCourt.get(0).getCourtId().toString()),
//                    Updates.inc("rented", -1));
//
//            clientSession.commitTransaction();
//        } catch (Exception exception) {
//            clientSession.abortTransaction();
//            clientSession.close();
//            court.setRented(true);
//            throw new MyMongoException(exception.getMessage());
//        } finally {
//            clientSession.close();
//        }
//    }
