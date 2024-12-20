package org.yearup.data;


import org.yearup.models.Category;
import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile getByUserId(int id);
    void update(int profileId, Profile profile);
}
