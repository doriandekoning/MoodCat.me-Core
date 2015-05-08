package me.moodcat.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import me.moodcat.database.controllers.SongDAO;
import me.moodcat.database.embeddables.VAVector;
import me.moodcat.database.entities.Song;
import me.moodcat.mood.Mood;
import me.moodcat.mood.classifier.MoodClassifier;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * API that can process a list of songs using {@link #processSongs()}.
 * 
 * @author JeremybellEU
 */
@Singleton
@Path("/api/songs/process")
@Produces(MediaType.TEXT_PLAIN)
public class ProcessAPI {

    private SongDAO songDAO;

    private MoodClassifier classifier;

    private Processingstate processingState;

    @Inject
    @VisibleForTesting
    public ProcessAPI(final SongDAO songDAO, final MoodClassifier classifier) {
        this.songDAO = songDAO;
        this.classifier = classifier;
        this.processingState = new Idle();
    }

    /**
     * Process songs to determine the {@link VAVector VAVectors} which represent the closest
     * {@link Mood}.
     */
    @GET
    @Transactional
    public void processSongs() {
        this.processingState.process();
    }

    /**
     * Interface to implement state pattern for {@link ProcessAPI#processingState}.
     *
     * @author JeremybellEU
     */
    interface Processingstate {

        /**
         * Try to process the next song list.
         */
        void process();
    }

    /**
     * State indicating we can't process new songs just yet.
     *
     * @author JeremybellEU
     */
    private class Processing implements Processingstate {

        @Override
        public void process() {
            // When we are already processing we can't process again.
        }

    }

    /**
     * State indicating we can process new songs.
     *
     * @author JeremybellEU
     */
    private class Idle implements Processingstate {

        @Override
        public void process() {
            ProcessAPI.this.processingState = new Processing();

            final List<Song> songs = ProcessAPI.this.songDAO.findNextUnprocessedSongs();

            songs.parallelStream()
                    .forEach((song) -> {
                        final VAVector vector = ProcessAPI.this.classifier.predict(song.getFeatures());

                        song.setValenceArousal(vector);
                    });

            songs.stream()
                    .forEach(ProcessAPI.this.songDAO::merge);

            ProcessAPI.this.processingState = new Idle();
        }
    }
}
