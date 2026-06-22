/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyDatabaseMetaData;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Wrapper;

public class HikariProxyDatabaseMetaData
extends ProxyDatabaseMetaData
implements Wrapper,
DatabaseMetaData {
    public boolean isWrapperFor(Class clazz) throws SQLException {
        try {
            return this.delegate.isWrapperFor(clazz);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        try {
            return this.delegate.allProceduresAreCallable();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        try {
            return this.delegate.allTablesAreSelectable();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getURL() throws SQLException {
        try {
            return this.delegate.getURL();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getUserName() throws SQLException {
        try {
            return this.delegate.getUserName();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        try {
            return this.delegate.isReadOnly();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        try {
            return this.delegate.nullsAreSortedHigh();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        try {
            return this.delegate.nullsAreSortedLow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        try {
            return this.delegate.nullsAreSortedAtStart();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        try {
            return this.delegate.nullsAreSortedAtEnd();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        try {
            return this.delegate.getDatabaseProductName();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        try {
            return this.delegate.getDatabaseProductVersion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getDriverName() throws SQLException {
        try {
            return this.delegate.getDriverName();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getDriverVersion() throws SQLException {
        try {
            return this.delegate.getDriverVersion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getDriverMajorVersion() {
        return this.delegate.getDriverMajorVersion();
    }

    @Override
    public int getDriverMinorVersion() {
        return this.delegate.getDriverMinorVersion();
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        try {
            return this.delegate.usesLocalFiles();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        try {
            return this.delegate.usesLocalFilePerTable();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        try {
            return this.delegate.supportsMixedCaseIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        try {
            return this.delegate.storesUpperCaseIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        try {
            return this.delegate.storesLowerCaseIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        try {
            return this.delegate.storesMixedCaseIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.delegate.supportsMixedCaseQuotedIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.delegate.storesUpperCaseQuotedIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.delegate.storesLowerCaseQuotedIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.delegate.storesMixedCaseQuotedIdentifiers();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        try {
            return this.delegate.getIdentifierQuoteString();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        try {
            return this.delegate.getSQLKeywords();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        try {
            return this.delegate.getNumericFunctions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getStringFunctions() throws SQLException {
        try {
            return this.delegate.getStringFunctions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        try {
            return this.delegate.getSystemFunctions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        try {
            return this.delegate.getTimeDateFunctions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        try {
            return this.delegate.getSearchStringEscape();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        try {
            return this.delegate.getExtraNameCharacters();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        try {
            return this.delegate.supportsAlterTableWithAddColumn();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        try {
            return this.delegate.supportsAlterTableWithDropColumn();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        try {
            return this.delegate.supportsColumnAliasing();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        try {
            return this.delegate.nullPlusNonNullIsNull();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        try {
            return this.delegate.supportsConvert();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsConvert(int n, int n2) throws SQLException {
        try {
            return this.delegate.supportsConvert(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        try {
            return this.delegate.supportsTableCorrelationNames();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        try {
            return this.delegate.supportsDifferentTableCorrelationNames();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        try {
            return this.delegate.supportsExpressionsInOrderBy();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        try {
            return this.delegate.supportsOrderByUnrelated();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        try {
            return this.delegate.supportsGroupBy();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        try {
            return this.delegate.supportsGroupByUnrelated();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        try {
            return this.delegate.supportsGroupByBeyondSelect();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        try {
            return this.delegate.supportsLikeEscapeClause();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        try {
            return this.delegate.supportsMultipleResultSets();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        try {
            return this.delegate.supportsMultipleTransactions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        try {
            return this.delegate.supportsNonNullableColumns();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        try {
            return this.delegate.supportsMinimumSQLGrammar();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        try {
            return this.delegate.supportsCoreSQLGrammar();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        try {
            return this.delegate.supportsExtendedSQLGrammar();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        try {
            return this.delegate.supportsANSI92EntryLevelSQL();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        try {
            return this.delegate.supportsANSI92IntermediateSQL();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        try {
            return this.delegate.supportsANSI92FullSQL();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        try {
            return this.delegate.supportsIntegrityEnhancementFacility();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        try {
            return this.delegate.supportsOuterJoins();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        try {
            return this.delegate.supportsFullOuterJoins();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        try {
            return this.delegate.supportsLimitedOuterJoins();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        try {
            return this.delegate.getSchemaTerm();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        try {
            return this.delegate.getProcedureTerm();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        try {
            return this.delegate.getCatalogTerm();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        try {
            return this.delegate.isCatalogAtStart();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        try {
            return this.delegate.getCatalogSeparator();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        try {
            return this.delegate.supportsSchemasInDataManipulation();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        try {
            return this.delegate.supportsSchemasInProcedureCalls();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        try {
            return this.delegate.supportsSchemasInTableDefinitions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        try {
            return this.delegate.supportsSchemasInIndexDefinitions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        try {
            return this.delegate.supportsSchemasInPrivilegeDefinitions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        try {
            return this.delegate.supportsCatalogsInDataManipulation();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        try {
            return this.delegate.supportsCatalogsInProcedureCalls();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        try {
            return this.delegate.supportsCatalogsInTableDefinitions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        try {
            return this.delegate.supportsCatalogsInIndexDefinitions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        try {
            return this.delegate.supportsCatalogsInPrivilegeDefinitions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        try {
            return this.delegate.supportsPositionedDelete();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        try {
            return this.delegate.supportsPositionedUpdate();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        try {
            return this.delegate.supportsSelectForUpdate();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        try {
            return this.delegate.supportsStoredProcedures();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        try {
            return this.delegate.supportsSubqueriesInComparisons();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        try {
            return this.delegate.supportsSubqueriesInExists();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        try {
            return this.delegate.supportsSubqueriesInIns();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        try {
            return this.delegate.supportsSubqueriesInQuantifieds();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        try {
            return this.delegate.supportsCorrelatedSubqueries();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        try {
            return this.delegate.supportsUnion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        try {
            return this.delegate.supportsUnionAll();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        try {
            return this.delegate.supportsOpenCursorsAcrossCommit();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        try {
            return this.delegate.supportsOpenCursorsAcrossRollback();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        try {
            return this.delegate.supportsOpenStatementsAcrossCommit();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        try {
            return this.delegate.supportsOpenStatementsAcrossRollback();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        try {
            return this.delegate.getMaxBinaryLiteralLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        try {
            return this.delegate.getMaxCharLiteralLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        try {
            return this.delegate.getMaxColumnNameLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        try {
            return this.delegate.getMaxColumnsInGroupBy();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        try {
            return this.delegate.getMaxColumnsInIndex();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        try {
            return this.delegate.getMaxColumnsInOrderBy();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        try {
            return this.delegate.getMaxColumnsInSelect();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        try {
            return this.delegate.getMaxColumnsInTable();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxConnections() throws SQLException {
        try {
            return this.delegate.getMaxConnections();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        try {
            return this.delegate.getMaxCursorNameLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        try {
            return this.delegate.getMaxIndexLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        try {
            return this.delegate.getMaxSchemaNameLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        try {
            return this.delegate.getMaxProcedureNameLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        try {
            return this.delegate.getMaxCatalogNameLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        try {
            return this.delegate.getMaxRowSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        try {
            return this.delegate.doesMaxRowSizeIncludeBlobs();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        try {
            return this.delegate.getMaxStatementLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxStatements() throws SQLException {
        try {
            return this.delegate.getMaxStatements();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        try {
            return this.delegate.getMaxTableNameLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        try {
            return this.delegate.getMaxTablesInSelect();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        try {
            return this.delegate.getMaxUserNameLength();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        try {
            return this.delegate.getDefaultTransactionIsolation();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        try {
            return this.delegate.supportsTransactions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int n) throws SQLException {
        try {
            return this.delegate.supportsTransactionIsolationLevel(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        try {
            return this.delegate.supportsDataDefinitionAndDataManipulationTransactions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        try {
            return this.delegate.supportsDataManipulationTransactionsOnly();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        try {
            return this.delegate.dataDefinitionCausesTransactionCommit();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        try {
            return this.delegate.dataDefinitionIgnoredInTransactions();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getProcedures(String string, String string2, String string3) throws SQLException {
        try {
            return super.getProcedures(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getProcedureColumns(String string, String string2, String string3, String string4) throws SQLException {
        try {
            return super.getProcedureColumns(string, string2, string3, string4);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getTables(String string, String string2, String string3, String[] stringArray) throws SQLException {
        try {
            return super.getTables(string, string2, string3, stringArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        try {
            return super.getSchemas();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        try {
            return super.getCatalogs();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        try {
            return super.getTableTypes();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getColumns(String string, String string2, String string3, String string4) throws SQLException {
        try {
            return super.getColumns(string, string2, string3, string4);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getColumnPrivileges(String string, String string2, String string3, String string4) throws SQLException {
        try {
            return super.getColumnPrivileges(string, string2, string3, string4);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getTablePrivileges(String string, String string2, String string3) throws SQLException {
        try {
            return super.getTablePrivileges(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getBestRowIdentifier(String string, String string2, String string3, int n, boolean bl) throws SQLException {
        try {
            return super.getBestRowIdentifier(string, string2, string3, n, bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getVersionColumns(String string, String string2, String string3) throws SQLException {
        try {
            return super.getVersionColumns(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getPrimaryKeys(String string, String string2, String string3) throws SQLException {
        try {
            return super.getPrimaryKeys(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getImportedKeys(String string, String string2, String string3) throws SQLException {
        try {
            return super.getImportedKeys(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getExportedKeys(String string, String string2, String string3) throws SQLException {
        try {
            return super.getExportedKeys(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getCrossReference(String string, String string2, String string3, String string4, String string5, String string6) throws SQLException {
        try {
            return super.getCrossReference(string, string2, string3, string4, string5, string6);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        try {
            return super.getTypeInfo();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getIndexInfo(String string, String string2, String string3, boolean bl, boolean bl2) throws SQLException {
        try {
            return super.getIndexInfo(string, string2, string3, bl, bl2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsResultSetType(int n) throws SQLException {
        try {
            return this.delegate.supportsResultSetType(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsResultSetConcurrency(int n, int n2) throws SQLException {
        try {
            return this.delegate.supportsResultSetConcurrency(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean ownUpdatesAreVisible(int n) throws SQLException {
        try {
            return this.delegate.ownUpdatesAreVisible(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean ownDeletesAreVisible(int n) throws SQLException {
        try {
            return this.delegate.ownDeletesAreVisible(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean ownInsertsAreVisible(int n) throws SQLException {
        try {
            return this.delegate.ownInsertsAreVisible(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean othersUpdatesAreVisible(int n) throws SQLException {
        try {
            return this.delegate.othersUpdatesAreVisible(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean othersDeletesAreVisible(int n) throws SQLException {
        try {
            return this.delegate.othersDeletesAreVisible(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean othersInsertsAreVisible(int n) throws SQLException {
        try {
            return this.delegate.othersInsertsAreVisible(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean updatesAreDetected(int n) throws SQLException {
        try {
            return this.delegate.updatesAreDetected(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean deletesAreDetected(int n) throws SQLException {
        try {
            return this.delegate.deletesAreDetected(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean insertsAreDetected(int n) throws SQLException {
        try {
            return this.delegate.insertsAreDetected(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        try {
            return this.delegate.supportsBatchUpdates();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getUDTs(String string, String string2, String string3, int[] nArray) throws SQLException {
        try {
            return super.getUDTs(string, string2, string3, nArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        try {
            return this.delegate.supportsSavepoints();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        try {
            return this.delegate.supportsNamedParameters();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        try {
            return this.delegate.supportsMultipleOpenResults();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        try {
            return this.delegate.supportsGetGeneratedKeys();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getSuperTypes(String string, String string2, String string3) throws SQLException {
        try {
            return super.getSuperTypes(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getSuperTables(String string, String string2, String string3) throws SQLException {
        try {
            return super.getSuperTables(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getAttributes(String string, String string2, String string3, String string4) throws SQLException {
        try {
            return super.getAttributes(string, string2, string3, string4);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsResultSetHoldability(int n) throws SQLException {
        try {
            return this.delegate.supportsResultSetHoldability(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            return this.delegate.getResultSetHoldability();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        try {
            return this.delegate.getDatabaseMajorVersion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        try {
            return this.delegate.getDatabaseMinorVersion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        try {
            return this.delegate.getJDBCMajorVersion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        try {
            return this.delegate.getJDBCMinorVersion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getSQLStateType() throws SQLException {
        try {
            return this.delegate.getSQLStateType();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        try {
            return this.delegate.locatorsUpdateCopy();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        try {
            return this.delegate.supportsStatementPooling();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        try {
            return this.delegate.getRowIdLifetime();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getSchemas(String string, String string2) throws SQLException {
        try {
            return super.getSchemas(string, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        try {
            return this.delegate.supportsStoredFunctionsUsingCallSyntax();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        try {
            return this.delegate.autoCommitFailureClosesAllResultSets();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        try {
            return super.getClientInfoProperties();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getFunctions(String string, String string2, String string3) throws SQLException {
        try {
            return super.getFunctions(string, string2, string3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getFunctionColumns(String string, String string2, String string3, String string4) throws SQLException {
        try {
            return super.getFunctionColumns(string, string2, string3, string4);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getPseudoColumns(String string, String string2, String string3, String string4) throws SQLException {
        try {
            return super.getPseudoColumns(string, string2, string3, string4);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        try {
            return this.delegate.generatedKeyAlwaysReturned();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getMaxLogicalLobSize() throws SQLException {
        try {
            return this.delegate.getMaxLogicalLobSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean supportsRefCursors() throws SQLException {
        try {
            return this.delegate.supportsRefCursors();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    HikariProxyDatabaseMetaData(ProxyConnection proxyConnection, DatabaseMetaData databaseMetaData) {
        super(proxyConnection, databaseMetaData);
    }
}

