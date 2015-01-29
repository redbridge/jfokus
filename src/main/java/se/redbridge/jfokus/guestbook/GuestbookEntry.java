/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package se.redbridge.jfokus.guestbook;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "GUESTBOOK_ENTRY")
public class GuestbookEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "ID")
  private Long id;

  @Column(name = "MESSAGE", length = 300)
  @NotNull
  @Size(min = 2, max = 300)
  private String message;

  @Column(name = "AUTHOR", length = 30)
  @NotNull
  @Size(min = 2, max = 30)
  private String author;

  @Column(name = "CREATED")
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date created;

  public GuestbookEntry() {
    // intentionally left empty
  }

  public GuestbookEntry(final String message, final String author) {
    this.message = message;
    this.author = author;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(final String author) {
    this.author = author;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(final Date created) {
    this.created = created;
  }

  @PrePersist
  private void onPrePersist() {
    created = new Date();
  }

  @Override
  public boolean equals(final Object o) {
    boolean retVal = o == this;

    if (!retVal && o != null && getClass() == o.getClass()) {
      final GuestbookEntry that = (GuestbookEntry) o;

      retVal = Objects.equals(getId(), that.getId()) &&
               Objects.equals(getMessage(), that.getMessage()) &&
               Objects.equals(getAuthor(), that.getAuthor()) &&
               Objects.equals(getCreated(), that.getCreated());
    }

    return retVal;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getMessage(), getAuthor(), getCreated());
  }

  @Override
  public String toString() {
    return "GuestbookEntry{" +
           "id=" + getId() +
           ", message='" + getMessage() + '\'' +
           ", author='" + getAuthor() + '\'' +
           ", created=" + getCreated() +
           '}';
  }
}
