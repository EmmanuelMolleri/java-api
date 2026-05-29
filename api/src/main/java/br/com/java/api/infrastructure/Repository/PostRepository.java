package br.com.java.api.infrastructure.Repository;

import br.com.java.api.domain.entities.Post;
import br.com.java.api.domain.Enums.PostVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        select p from Post p
        where p.author.id in :authorIds
        order by p.createdAt desc
        """)
    Page<Post> findFeedPosts(@Param("authorIds") Collection<Long> authorIds, Pageable pageable);

    @Query("""
        select p from Post p
        where p.author.id = :authorId
          and p.visibility = :visibility
        order by p.createdAt desc
        """)
    Page<Post> findByAuthorAndVisibility(
        @Param("authorId") Long authorId,
        @Param("visibility") PostVisibility visibility,
        Pageable pageable
    );

    @Query("""
        select p from Post p
        where p.author.id = :authorId
        order by p.createdAt desc
        """)
    Page<Post> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);
}
