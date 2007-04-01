/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sparql.engine.ref;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.Table;
import com.hp.hpl.jena.sparql.core.DataSourceGraphImpl;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.engine.*;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorCheck;
import com.hp.hpl.jena.sparql.util.Context;


public class QueryEngineRef extends QueryEngineOpBase
{
    static public QueryEngineFactory getFactory()   { return factory ; } 
    static public void register()       { QueryEngineRegistry.addFactory(factory) ; }
    static public void unregister()     { QueryEngineRegistry.removeFactory(factory) ; }
    
    public QueryEngineRef(Query q)
    { this(q, null) ; }

    public QueryEngineRef(Query q, Context context)
    { super(q, context) ; }
    
    protected QueryIterator createQueryIterator(Op op)
    {
        ExecutionContext execCxt = getExecContext() ;
        Evaluator eval = EvaluatorFactory.create(execCxt) ;
        Table table = Eval.eval(eval, op) ;
        return table.iterator(execCxt) ;
    }


    public static QueryIterator eval(Op op, Graph graph)
    {
        return eval(op, new DataSourceGraphImpl(graph)) ;
    }
    
    public static QueryIterator eval(Op op, DatasetGraph dsg)
    {
        ExecutionContext execCxt = new ExecutionContext(ARQ.getContext(), null, dsg.getDefaultGraph(), dsg) ;
        Evaluator eval = EvaluatorFactory.create(execCxt) ;
        Table table = Eval.eval(eval, op) ;
        // If ref eval becomes non-iterator-ish, remove this.
        return QueryIteratorCheck.check(table.iterator(execCxt), execCxt) ;
        //return table.iterator(execCxt) ;
    }
    
    private static QueryEngineFactory factory = new QueryEngineFactory()
    {
        public boolean accept(Query query, Dataset dataset) 
        { return true ; }

        public QueryExecution create(Query query, Dataset dataset)
        {
            QueryEngineRef engine = new QueryEngineRef(query) ;
            engine.setDataset(dataset) ;
            return engine ;
        }
    } ;
}
/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */