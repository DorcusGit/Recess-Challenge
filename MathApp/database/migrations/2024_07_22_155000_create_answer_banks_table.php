<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateAnswerBanksTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('answer_banks', function (Blueprint $table) {
            $table->id('answerBankID'); // Auto-incrementing primary key
            $table->unsignedBigInteger('challengeNo'); // Foreign key
            $table->foreign('challengeNo')
                  ->references('id')
                  ->on('challenges') // Updated table name
                  ->onDelete('cascade');
            
            $table->unsignedBigInteger('questionBankID')->nullable(); // Foreign key
            $table->foreign('questionBankID')
                  ->references('id')
                  ->on('question_banks') // Updated table name
                  ->onDelete('set null');
            $table->timestamps(); // Adds created_at and updated_at columns
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('answer_banks');
    }
}