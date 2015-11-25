/*
 * Copyright 2015 JAXIO http://www.jaxio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaxio.jpa.querybyexample;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.util.Collection;

/**
 * The EntityGraphLoader is used to load within a single read-only transaction all the desired associations that
 * are normally lazily loaded.
 */
public abstract class EntityGraphLoader<T extends Identifiable<PK>, PK extends Serializable> {

    protected GenericRepository<T, PK> repository;

    @Resource
    private UserTransaction tx;

    public EntityGraphLoader() {
        // required no-args construct to make this bean is proxyable
    }

    public EntityGraphLoader(GenericRepository<T, PK> repository) {
        this.repository = repository;
    }

    /*
     * Get the entity by id and load its graph using loadGraph.
     */
    public T getById(PK pk) {
        try {
            tx.begin();
            T entity = repository.getById(pk);
            loadGraph(entity);
            tx.commit();
            return entity;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    /**
     * Merge the passed entity in order to attach it to the current persistent context,
     * then load its graph. Once done, detach it and rollback the transaction to avoid the propagation
     * to the database of any pending modification. This could be considered as a _HACK_ but it does the job.
     */
    public T merge(T entity) {
        try {
            tx.begin();
            T mergedEntity = repository.merge(entity);
            loadGraph(mergedEntity);
            // we do not want to propagate any pending modification
            repository.detach(mergedEntity);
            tx.rollback();
            return mergedEntity;
        } catch (NotSupportedException nse) {
            try {
                tx.rollback();
            } catch (Exception x) {
            }
            System.out.println("==============> TODO: handle properly " + nse.getMessage());
        } catch (SystemException se) {
            try {
                tx.rollback();
            } catch (Exception x) {
            }
            System.out.println("==============> TODO: handle properly " + se.getMessage());
        }
        return entity;
    }

    /*
     * Load whatever is needed in the graph of the passed entity, for example x-to-many collection, x-to-one object, etc.
     */
    public abstract void loadGraph(T entity);

    /*
     * Load the passed 'x-to-many' association.
     */
    protected void loadCollection(Collection<?> collection) {
        if (collection != null) {
            collection.size();
        }
    }

    /*
     * Load the passed 'x-to-one' association.
     */
    protected void loadSingular(Object association) {
        if (association != null) {
            association.toString();
        }
    }
}