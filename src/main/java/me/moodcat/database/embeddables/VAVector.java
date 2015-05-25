package me.moodcat.database.embeddables;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Valence/Arousal vector class.
 *
 * @author Jan-Willem Gmelig Meyling
 */
@Data
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class VAVector {

    /**
     * The valence in the range of [-1, 1].
     */
    @Column(name = "valence")
    private double valence;

    /**
     * The arousal of the song in the range of [-1, 1].
     */
    @Column(name = "arousal")
    private double arousal;

    /**
     * Constructor to create a vector. Asserts that the provided valence and arousal are in the
     * specified range.
     *
     * @param valence
     *            The valence of this vector.
     * @param arousal
     *            The arousal of this vector.
     */
    public VAVector(final double valence, final double arousal) {
        assert valence >= -1.0 && valence <= 1.0 : "Valence must be in [-1,1] range.";
        assert arousal >= -1.0 && arousal <= 1.0 : "Arousal must be in [-1,1] range.";

        this.setValence(valence);
        this.setArousal(arousal);
    }

    /**
     * Add this VAVector to another VAVector.
     *
     * @param other
     *            Another VAVector
     * @return a new VAVector
     */
    public VAVector add(final VAVector other) {
        return new VAVector(this.getValence() + other.getValence(), this.getArousal()
                + other.getArousal());
    }

    /**
     * Subtract another vector from this VAVector.
     *
     * @param other
     *            Another VAVector
     * @return a new VAVector
     */
    public VAVector subtract(final VAVector other) {
        return new VAVector(this.getValence() - other.getValence(), this.getArousal()
                - other.getArousal());
    }

    /**
     * Multiply this VAVector with another VAVector.
     *
     * @param other
     *            Another VAVector
     * @return a new VAVector
     */
    public VAVector multiply(final VAVector other) {
        return new VAVector(this.getValence() * other.getValence(), this.getArousal()
                * other.getArousal());
    }

    /**
     * Multiply this vector with a scalar.
     *
     * @param scalar
     *            The multiplier.
     * @return VAVector
     */
    public VAVector multiply(final double scalar) {
        return new VAVector(this.getValence() * scalar, this.getArousal() * scalar);
    }

    /**
     * Get the distance between two vectors.
     *
     * @param other
     *            Another VAVector
     * @return the distance
     */
    public double distance(final VAVector other) {
        final double a = other.getValence() - this.getValence();
        final double b = other.getArousal() - this.getArousal();
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    /**
     * Get the distance between two vectors.
     *
     * @param a
     *            first vector
     * @param b
     *            second vector
     * @return the distance
     */
    public static double distance(final VAVector one, final VAVector other) {
        return one.distance(other);
    }

}
