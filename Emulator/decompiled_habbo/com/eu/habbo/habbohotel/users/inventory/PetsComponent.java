/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetsComponent.class);
    private final TIntObjectMap<Pet> pets = TCollections.synchronizedMap(new TIntObjectHashMap());

    public PetsComponent(Habbo habbo) {
        this.loadPets(habbo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadPets(Habbo habbo) {
        TIntObjectMap<Pet> tIntObjectMap = this.pets;
        synchronized (tIntObjectMap) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_pets WHERE user_id = ? AND room_id = 0");){
                statement.setInt(1, habbo.getHabboInfo().getId());
                try (ResultSet set = statement.executeQuery();){
                    while (set.next()) {
                        this.pets.put(set.getInt("id"), PetManager.loadPet(set));
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public Pet getPet(int id) {
        return this.pets.get(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPet(Pet pet) {
        TIntObjectMap<Pet> tIntObjectMap = this.pets;
        synchronized (tIntObjectMap) {
            this.pets.put(pet.getId(), pet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPets(Set<Pet> pets) {
        TIntObjectMap<Pet> tIntObjectMap = this.pets;
        synchronized (tIntObjectMap) {
            for (Pet p : pets) {
                this.pets.put(p.getId(), p);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePet(Pet pet) {
        TIntObjectMap<Pet> tIntObjectMap = this.pets;
        synchronized (tIntObjectMap) {
            this.pets.remove(pet.getId());
        }
    }

    public TIntObjectMap<Pet> getPets() {
        return this.pets;
    }

    public int getPetsCount() {
        return this.pets.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        TIntObjectMap<Pet> tIntObjectMap = this.pets;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<Pet> petIterator = this.pets.iterator();
            int i = this.pets.size();
            while (i-- > 0) {
                try {
                    petIterator.advance();
                }
                catch (NoSuchElementException e) {
                    break;
                }
                if (!petIterator.value().needsUpdate) continue;
                Emulator.getThreading().run(petIterator.value());
            }
        }
    }
}

